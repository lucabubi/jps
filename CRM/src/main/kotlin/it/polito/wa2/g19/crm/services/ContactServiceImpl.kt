package it.polito.wa2.g19.crm.services

import it.polito.wa2.g19.crm.config.SecurityConfig
import it.polito.wa2.g19.crm.dtos.*
import it.polito.wa2.g19.crm.entities.Category
import it.polito.wa2.g19.crm.entities.Contact
import it.polito.wa2.g19.crm.exceptions.*
import it.polito.wa2.g19.crm.repositories.*
import jakarta.transaction.Transactional
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import mu.KotlinLogging
import java.time.LocalDateTime

@Service
@Transactional
class ContactServiceImpl(
    private val contactRepository: ContactRepository,
    private val emailRepository: EmailRepository,
    private val telephoneRepository: TelephoneRepository,
    private val addressRepository: AddressRepository,
    private val noteRepository: NoteRepository
) : ContactService {
    private val logger = KotlinLogging.logger {}

    override fun getContacts(pageable: Pageable, name: String?, surname: String?, email: String?, phoneNumber: String?): List<ContactDTO> {
        val contacts = contactRepository.findAll(pageable.sort).toMutableList()
        if(email != null) {
            val emailContacts = emailRepository.findByEmailContaining(email).map { it.contact }
            contacts.retainAll(emailContacts)
        }
        if(phoneNumber != null) {
            val phoneContacts = telephoneRepository.findByTelephoneContaining(phoneNumber).map { it.contact }
            contacts.retainAll(phoneContacts)
        }
        if(name != null) {
            val nameContacts = contactRepository.findByNameContaining(name, pageable).content
            contacts.retainAll(nameContacts)
        }
        if(surname != null) {
            val surnameContacts = contactRepository.findBySurnameContaining(surname, pageable).content
            contacts.retainAll(surnameContacts)
        }
        return contacts.take(pageable.pageSize).map(Contact::toDTO)
    }

    override fun getContact(contactId: Long): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException("Contact with id: $contactId not found") }
        return contact.toDTO()
    }

    override fun createContact(createContactDTO: CreateContactDTO): ContactDTO {
        val noteList: List<NoteDTO>
        //Duplicated Data handling
        if (createContactDTO.emails.size != createContactDTO.emails.toSet().size)
            throw DuplicatedDataException("You're sending duplicated emails!")
        if (createContactDTO.telephones.size != createContactDTO.telephones.toSet().size)
            throw DuplicatedDataException("You're sending duplicated telephone numbers!")
        if (createContactDTO.addresses.size != createContactDTO.addresses.toSet().size)
            throw DuplicatedDataException("You're sending duplicated addresses!")

        logger.info { "Creating contact ${createContactDTO.name} ${createContactDTO.surname}..." }
        when (createContactDTO.category){
            Category.PROFESSIONAL -> {
                noteList = listOf(NoteDTO(
                    title = "Operator ${SecurityConfig.SecurityUtils.getUserFullName()} created a PROFESSIONAL contact",
                    createdAt = LocalDateTime.now(),
                    description = createContactDTO.noteDescription
                ))
            }
            Category.CUSTOMER -> {
                noteList = listOf(NoteDTO(
                    title = "Operator ${SecurityConfig.SecurityUtils.getUserFullName()} created a CUSTOMER contact",
                    createdAt = LocalDateTime.now(),
                    description = createContactDTO.noteDescription
                ))
            }
            else -> {
                noteList = listOf(NoteDTO(
                    title = "Operator ${SecurityConfig.SecurityUtils.getUserFullName()} created a UNKNOWN contact",
                    createdAt = LocalDateTime.now(),
                    description = createContactDTO.noteDescription
                ))
            }
        }
        val newContact = ContactDTO(
            name = createContactDTO.name,
            surname = createContactDTO.surname,
            ssn = createContactDTO.ssn,
            region = createContactDTO.region,
            category = createContactDTO.category,
            notes = noteList.toSet(),
            emails = createContactDTO.emails.map { EmailDTO(email = it) }.toSet(),
            telephones = createContactDTO.telephones.map { TelephoneDTO(telephone = it) }.toSet(),
            addresses = createContactDTO.addresses.map { AddressDTO(
                address = it.address,
                zipCode = it.zipCode,
                city = it.city,
                country = it.country,
                ) }.toSet()
        ).toEntity()

        contactRepository.save(newContact)
        logger.info { "Contact: ${newContact.id} - ${newContact.name} ${newContact.surname} saved" }
        noteList.forEach { noteRepository.save(it.toEntity()) }
        return newContact.toDTO()
    }

    override fun updateContact(contactId: Long, updateContactDTO: UpdateContactDTO): ContactDTO {
        val previousContact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException("Contact not found!") }
        logger.info { "Updating contact ${previousContact.name} ${previousContact.surname}..." }
        previousContact.name = updateContactDTO.name ?: previousContact.name
        previousContact.surname = updateContactDTO.surname ?: previousContact.surname
        previousContact.ssn = updateContactDTO.ssn ?: previousContact.ssn

        logger.info { "Contact updated!" }
        return previousContact.toDTO()
    }

    override fun deleteContact(contactId: Long) {
        val contact = contactRepository.findById(contactId).orElseThrow{ ContactNotFoundException("Contact not found!") }
        logger.info { "Deleting contact ${contact.id} ${contact.name} ${contact.surname}..." }
        contactRepository.deleteById(contactId)
        logger.info { "Contact deleted!"}
    }

    override fun addEmailToContact(contactId: Long, email: String): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException("Contact not found!") }
        val contactEmails = contact.emails.toMutableSet()
        val logNote = NoteDTO(
            title = "Operator ${SecurityConfig.SecurityUtils.getUserFullName()} added email $email to contact ${contact.name} ${contact.surname}",
            createdAt = LocalDateTime.now(),
            description = null
        )
        contact.notes.add(logNote.toEntity())
        if (contactEmails.any { it.email == email })
            throw DuplicatedDataException("Email already present!")
        logger.info { "Creating email: ${email}..." }
        contactEmails.add(EmailDTO(email = email).toEntity(contact))
        logger.info { "Saving $email to contact: ${contact.id} ${contact.name} ${contact.surname}..." }
        // Why not converting back again from mutable Set to immutable Set? (ex. contact.emails = contactEmails.toSet()) Spring Data JPA handles the conversion automatically!
        contact.emails = contactEmails
        logger.info { "Email added to contact!" }
        return contact.toDTO()
    }

    override fun updateEmail(contactId: Long, emailId: Long, email: String): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException("Contact not found!") }
        val contactEmails = contact.emails.toMutableSet()
        if (contactEmails.any { it.email == email })
            throw DuplicatedDataException("Email already present!")
        contactEmails.firstOrNull { it.id == emailId }?.also { it.email = email }
            ?: throw EmailNotFoundException("Email not found!")
        logger.info { "Updating $email with id: $emailId from contact: ${contact.id} ${contact.name} ${contact.surname}..." }
        // Why not converting back again from mutable Set to immutable Set? (ex. contact.emails = contactEmails.toSet()) Spring Data JPA handles the conversion automatically!
        val logNote = NoteDTO(
            title = "Operator ${SecurityConfig.SecurityUtils.getUserFullName()} updated email $emailId to contact ${contact.name} ${contact.surname}",
            createdAt = LocalDateTime.now(),
            description = null
        )
        contact.notes.add(logNote.toEntity())
        contact.emails = contactEmails
        logger.info { "Email updated!" }
        return contact.toDTO()
    }

    override fun deleteEmail(contactId: Long, emailId: Long): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException("Contact not found!") }
        contact.emails.find { it.id == emailId } ?: throw EmailNotFoundException("Email not found!")
        val filteredEmails = contact.emails.filter { it.id != emailId }.toMutableSet()
        logger.info { "Deleting email with id: $emailId from contact: ${contact.id} ${contact.name} ${contact.surname}..." }
        contact.emails = filteredEmails
        val logNote = NoteDTO(
            title = "Operator ${SecurityConfig.SecurityUtils.getUserFullName()} removed email $emailId from contact ${contact.name} ${contact.surname}",
            createdAt = LocalDateTime.now(),
            description = null
        )
        contact.notes.add(logNote.toEntity())
        emailRepository.deleteById(emailId)
        logger.info { "Email deleted!" }
        return contact.toDTO()
    }

    override fun updateCategory(contactId: Long, category: Category): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException("Contact not found!") }
        val logNote = NoteDTO(
            title = "Operator ${SecurityConfig.SecurityUtils.getUserFullName()} updated category to contact ${contact.name} ${contact.surname}",
            createdAt = LocalDateTime.now(),
            description = "From ${contact.category} to $category"
        )
        logger.info { "Updating Category..." }
        contact.category = category
        contact.notes.add(logNote.toEntity())
        logger.info { "Category updated!" }
        return contact.toDTO()
    }

    override fun addTelephoneToContact(contactId: Long, telephone: String): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException("Contact not found!") }
        val contactTelephones = contact.telephones.toMutableSet()
        if (contactTelephones.any { it.telephone == telephone })
            throw DuplicatedDataException("Telephone number already present!")
        logger.info { "Creating telephone number: ${telephone}..." }
        contactTelephones.add(TelephoneDTO(telephone = telephone).toEntity(contact))
        logger.info { "Saving $telephone to contact: ${contact.id} ${contact.name} ${contact.surname}..." }
        val logNote = NoteDTO(
            title = "Operator ${SecurityConfig.SecurityUtils.getUserFullName()} added phone number to contact ${contact.name} ${contact.surname}",
            createdAt = LocalDateTime.now(),
            description = "Phone number: $telephone"
        )
        contact.telephones = contactTelephones
        contact.notes.add(logNote.toEntity())
        logger.info { "Telephone number added to contact!" }
        return contact.toDTO()
    }

    override fun updateTelephone(contactId: Long, telephoneId: Long, telephone: String): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException("Contact not found!") }
        val contactTelephones = contact.telephones.toMutableSet()
        if (contactTelephones.any { it.telephone == telephone })
            throw DuplicatedDataException("Telephone number already present!")
        contactTelephones.firstOrNull { it.id == telephoneId }?.also { it.telephone = telephone } ?: throw TelephoneNotFoundException("Telephone number not found!")
        logger.info { "Updating $telephone with id: $telephoneId from contact: ${contact.id} ${contact.name} ${contact.surname}..." }
        // Why not converting back again from mutable Set to immutable Set? (ex. contact.telephones = contactTelephones.toSet()) Spring Data JPA handles the conversion automatically!
        val logNote = NoteDTO(
            title = "Operator ${SecurityConfig.SecurityUtils.getUserFullName()} updated phone number $telephoneId to contact ${contact.name} ${contact.surname}",
            createdAt = LocalDateTime.now(),
            description = "Phone number: $telephone"
        )
        contact.notes.add(logNote.toEntity())
        contact.telephones = contactTelephones
        logger.info { "Telephone number updated!" }
        return contact.toDTO()
    }

    override fun deleteTelephone(contactId: Long, telephoneId: Long): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException("Contact not found!") }
        contact.telephones.firstOrNull { it.id == telephoneId } ?: throw TelephoneNotFoundException("Telephone number not found!")
        contact.telephones = contact.telephones.filter { it.id != telephoneId }.toMutableSet()
        logger.info { "Deleting telephone number with id: $telephoneId from contact: ${contact.id} ${contact.name} ${contact.surname}..." }
        val logNote = NoteDTO(
            title = "Operator ${SecurityConfig.SecurityUtils.getUserFullName()} deleted phone number $telephoneId from contact ${contact.name} ${contact.surname}",
            createdAt = LocalDateTime.now(),
            description = null
        )
        contact.notes.add(logNote.toEntity())
        telephoneRepository.deleteById(telephoneId)
        logger.info { "Telephone number deleted!" }
        return contact.toDTO()
    }

    override fun addAddressToContact(contactId: Long, address: AddressDTO): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow{ ContactNotFoundException("Contact not found!") }
        val contactAddresses = contact.addresses.toMutableSet()
        if(contactAddresses.any { it.id == address.id })
            throw DuplicatedDataException("Address already present!")
        logger.info { "Creating address ${address}..." }
        contactAddresses.add(AddressDTO(
            id = address.id,
            address = address.address,
            zipCode = address.zipCode,
            city = address.city,
            country = address.country
            ).toEntity(contact))
        logger.info { "Saving ${address.address} to contact: ${contact.id} ${contact.name} ${contact.surname}..." }
        val logNote = NoteDTO(
            title = "Operator ${SecurityConfig.SecurityUtils.getUserFullName()} added address to contact ${contact.name} ${contact.surname}",
            createdAt = LocalDateTime.now(),
            description = null
        )
        contact.notes.add(logNote.toEntity())
        contact.addresses = contactAddresses
        logger.info { "Address added to contact!" }
        return contact.toDTO()
    }

    override fun updateAddress( contactId: Long, addressId: Long, address: String): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException("Contact not found!") }
        val contactAddress = contact.addresses.toMutableSet()
        if (contactAddress.any { it.address == address })
            throw DuplicatedDataException("Address already present!")
        contactAddress.firstOrNull { it.id == addressId }?.also { it.address = address }
            ?: throw AddressNotFoundException("Address not found!")
        logger.info { "Updating $address with id: $addressId from contact: ${contact.id} ${contact.name} ${contact.surname}..." }
        val logNote = NoteDTO(
            title = "Operator ${SecurityConfig.SecurityUtils.getUserFullName()} updated address to contact ${contact.name} ${contact.surname}",
            createdAt = LocalDateTime.now(),
            description = null
        )
        contact.notes.add(logNote.toEntity())
        contact.addresses = contactAddress
        logger.info { "Address updated!" }
        return contact.toDTO()
    }

    override fun deleteAddress(contactId: Long, addressId: Long): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException("Contact not found!") }
        contact.addresses.find { it.id == addressId } ?: throw AddressNotFoundException("Address not found!")
        contact.addresses = contact.addresses.filter { it.id != addressId }.toMutableSet()
        // No need of contactRepository.save(contact) because of "dirty checking" managing entities
        logger.info { "Deleting address with id: $addressId from contact: ${contact.id} ${contact.name} ${contact.surname}..." }
        val logNote = NoteDTO(
            title = "Operator ${SecurityConfig.SecurityUtils.getUserFullName()} deleted address $addressId to contact ${contact.name} ${contact.surname}",
            createdAt = LocalDateTime.now(),
            description = null
        )
        contact.notes.add(logNote.toEntity())
        addressRepository.deleteById(addressId)
        logger.info { "Address deleted!" }
        return contact.toDTO()
    }
}