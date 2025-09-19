package it.polito.wa2.g19.crm.services

import it.polito.wa2.g19.crm.dtos.*
import it.polito.wa2.g19.crm.entities.*
import it.polito.wa2.g19.crm.exceptions.*
import it.polito.wa2.g19.crm.repositories.*
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import mu.KotlinLogging
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class CRMServiceImpl (
    private val customerRepository: CustomerRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val professionalRepository: ProfessionalRepository,
    private val messageRepository: MessageRepository,
    private val contactRepository: ContactRepository,
    private val telephoneRepository: TelephoneRepository,
    private val emailRepository: EmailRepository,
    private val addressRepository: AddressRepository
) : CRMService {
    private val logger = KotlinLogging.logger {}

    override fun createCustomer(customerDTO : CustomerDTO): CustomerDTO {
        // Convert DTO to entity
        val customer = Customer(
            contact = customerDTO.contact.toEntity(),
            notes = customerDTO.notes,
            jobOffers = customerDTO.jobOffers.map { it.toEntity() }.toSet()
        )
        logger.info("Creating customer: $customer")
        // Save to database
        val savedCustomer = customerRepository.save(customer)
        logger.info("Customer saved: $savedCustomer")
        // Convert entity back to DTO and return
        return savedCustomer.toDTO()
    }

    override fun getCustomers(): List<CustomerDTO> {
        logger.info("Retrieving all customers")
        // Retrieve customers from database
        val customers = customerRepository.findAll()
        logger.info("Customers retrieved: $customers")
        // Convert entities to DTOs and return
        return customers.map { it.toDTO() }
    }

    override fun getCustomer(customerId: Long): CustomerDTO {
        logger.info("Retrieving customer with id: $customerId")
        // Retrieve customer from database
        val customer = customerRepository.findById(customerId)
            .orElseThrow { CustomerNotFoundException("Customer with id $customerId not found") }
        logger.info("Customer retrieved: $customer")
        // Convert entity to DTO and return
        return customer.toDTO()
    }

    override fun updateCustomerNotes(id: Long, notes: List<String>) : CustomerDTO {
        val customer = customerRepository.findById(id).orElseThrow { CustomerNotFoundException("Contact with id $id not found") }
        logger.info { "Updating customer id:$id notes..." }
        customer.notes = notes
        // .save added for good practice, even if not needed because of "dirty checking" performed by Spring Data JPA
        customerRepository.save(customer)
        logger.info { "Customer id:$id notes updated" }
        return customer.toDTO()
    }

    override fun createProfessional(professionalDTO: ProfessionalDTO): ProfessionalDTO {
        // Convert DTO to entity
        val professional = Professional(
            contact = professionalDTO.contact.toEntity(),
            notes = professionalDTO.notes,
            skills = professionalDTO.skills,
            dailyRate = professionalDTO.dailyRate,
            employmentState = professionalDTO.employmentState,
            location = professionalDTO.location
        )
        logger.info("Creating professional: $professional")
        // Save to database
        val savedProfessional = professionalRepository.save(professional)
        logger.info("Professional saved: $savedProfessional")
        // Convert entity back to DTO and return
        return savedProfessional.toDTO()
    }

    override fun getProfessionals(
        pageable: Pageable,
        employmentState: Optional<Professional.State>,
        location: Optional<String>,
        skills: Optional<List<String>>
    ) : List<ProfessionalDTO> {
        logger.info { "Retrieving professionals..." }
        var professionals = professionalRepository.findAll(pageable).toList()
        logger.info { "Professionals retrieved!" }
        logger.info { "Applying filters..." }
        if (employmentState.isPresent) {
            professionals = professionals.filter { it.employmentState == employmentState.get() }
        }
        if (location.isPresent) {
            professionals = professionals.filter { it.location == location.get() }
        }
        if (skills.isPresent) {
            professionals = professionals.filter { it.skills.containsAll(skills.get()) }
        }
        logger.info { "Filters applied!" }
        return professionals.map { it.toDTO() }
    }

    override fun getProfessional(professionalId: Long): ProfessionalDTO {
        logger.info("Retrieving professional with id: $professionalId")
        // Retrieve professional from database
        val professional = professionalRepository.findById(professionalId)
            .orElseThrow { ProfessionalNotFoundException("Professional with id $professionalId not found") }
        logger.info("Professional retrieved: $professional")
        // Convert entity to DTO and return
        return professional.toDTO()
    }

    override fun updateProfessional(id: Long, updateDTO: ProfessionalUpdateDTO) : ProfessionalDTO {
        val professional = professionalRepository.findById(id)
            .orElseThrow { ProfessionalNotFoundException("Professional with id $id not found") }
        val activeJobOffer = professional.jobOffers.filter { it.status == JobOffer.Status.CONSOLIDATED }
        if(activeJobOffer.isNotEmpty() && updateDTO.employmentState.isPresent)
            throw ProfessionalNotAvailableException("Professional with id $id is currently working")
        logger.info { "Updating professional id:$id..." }
        updateDTO.notes.ifPresent { professional.notes = it }
        updateDTO.skills.ifPresent { professional.skills = it }
        updateDTO.dailyRate.ifPresent { professional.dailyRate = it }
        updateDTO.employmentState.ifPresent { professional.employmentState = it }
        updateDTO.location.ifPresent { professional.location = it }
        // .save added for good practice, even if not needed because of "dirty checking" performed by Spring Data JPA
        professionalRepository.save(professional)
        logger.info { "Professional id:$id updated" }
        return professional.toDTO()
    }
    override fun createJobOffer(createJobOfferDTO: CreateJobOfferDTO): JobOfferDTO {
        val customer = customerRepository.findById(createJobOfferDTO.customerId)
            .orElseThrow { CustomerNotFoundException("Customer with id ${createJobOfferDTO.customerId} not found") }

        val jobOffer = JobOfferDTO(
            description = createJobOfferDTO.description,
            duration = createJobOfferDTO.duration,
            notes = createJobOfferDTO.notes,
            requiredSkills = createJobOfferDTO.requiredSkills,
            customer = CustomerMinimalDTO(
                id = customer.id,
                contact = customer.contact.toDTO(),
                notes = customer.notes
            )
        ).toEntity()
        logger.info("Creating job offer: $jobOffer")
        val savedJobOffer = jobOfferRepository.save(jobOffer)
        logger.info("Job offer saved: $savedJobOffer ${savedJobOffer.customer.contact.id}")
        return savedJobOffer.toDTO()
    }


    override fun getJobOffers(pageable: Pageable, customerId: Long?, status: JobOffer.Status?, professionalId: Long?): List<JobOfferDTO> {
        var jobOffers = jobOfferRepository.findAll(pageable).toList()
        if (customerId != null) {
            jobOffers = jobOffers.filter { it.customer.id == customerId }
        }
        if (status != null) {
            jobOffers = jobOffers.filter { it.status == status }
        }
        if(professionalId != null){
            jobOffers = jobOffers.filter { it.professional?.id == professionalId }
        }
        return jobOffers.take(pageable.pageSize).map{ it.toDTO() }
    }

    override fun getOpenJobOffers(customerId: Long, pageable: Pageable): List<JobOfferDTO> {
        val customer = customerRepository.findById(customerId).orElseThrow { CustomerNotFoundException("Customer with id $customerId not found") }
        val jobOffers = customer.jobOffers.filter { !((it.status == JobOffer.Status.ABORTED)
                || (it.status == JobOffer.Status.CONSOLIDATED)
                || (it.status == JobOffer.Status.DONE)) }
        return jobOffers.take(pageable.pageSize).map{ it.toDTO() }

    }

    override fun getAcceptedJobOffers(professionalId: Long, pageable: Pageable): List<JobOfferDTO> {
        val professional = professionalRepository.findById(professionalId).orElseThrow { ProfessionalNotFoundException("Professional with id $professionalId not found") }
        val jobOffers = professional.jobOffers.filter { it.status == JobOffer.Status.CONSOLIDATED || it.status == JobOffer.Status.DONE }
        return jobOffers.take(pageable.pageSize).map { it.toDTO() }
    }

    override fun getAbortedJobOffers(pageable: Pageable, customerId: Long?, professionalId: Long?): List<JobOfferDTO> {
        var jobOffers = jobOfferRepository.findAll().filter { it.status == JobOffer.Status.ABORTED }
        if (customerId != null) {
            val customer = customerRepository.findById(customerId).orElseThrow { CustomerNotFoundException("Customer with id $customerId not found") }
            jobOffers = jobOffers.filter { it.customer == customer }
        }
        if (professionalId != null) {
            val professional = professionalRepository.findById(professionalId).orElseThrow { ProfessionalNotFoundException("Professional with id $professionalId not found") }
            jobOffers = jobOffers.filter { it.professional == professional }
        }
        return jobOffers.take(pageable.pageSize).map { it.toDTO() }
    }

    override fun updateJobOffer(jobOfferId: Long, requestDTO: JobOfferUpdateDTO): JobOfferDTO {
        val jobOffer = jobOfferRepository.findById(jobOfferId).orElseThrow { JobOfferNotFoundException("Job offer with id $jobOfferId not found") }
        logger.info("Updating job offer with id $jobOfferId")
        when (requestDTO.status) {
            "SELECTION_PHASE" -> {
                if(jobOffer.status == JobOffer.Status.ABORTED)
                    throw InvalidStatusException("Job offer status can't be ABORTED to apply change")
                if (requestDTO.professionalId.isPresent)
                    throw InvalidStatusException("Id of professional is not required for status SELECTION_PHASE")
                jobOffer.status = JobOffer.Status.SELECTION_PHASE
            }
            "CANDIDATE_PROPOSAL" -> {
                if(jobOffer.status != JobOffer.Status.SELECTION_PHASE)
                    throw InvalidStatusException("Job offer status must be SELECTION_PHASE to apply change")
                if (requestDTO.professionalId.isPresent)
                    throw InvalidStatusException("Id of professional is not required for status CANDIDATE_PROPOSAL")
                jobOffer.status = JobOffer.Status.CANDIDATE_PROPOSAL
            }
            "CONSOLIDATED" -> {
                if(jobOffer.status != JobOffer.Status.CANDIDATE_PROPOSAL)
                    throw InvalidStatusException("Job offer status must be CANDIDATE_PROPOSAL to apply change")
                if (!requestDTO.professionalId.isPresent)
                    throw InvalidStatusException("Id of professional is required for status CONSOLIDATED")
                val professional = professionalRepository.findById(requestDTO.professionalId.get())
                    .orElseThrow { ProfessionalNotFoundException("Professional not found") }
                if(professional.employmentState != Professional.State.AVAILABLE_FOR_WORK)
                    throw ProfessionalNotAvailableException("Professional with id ${professional.id} is currently working")
                professional.employmentState = Professional.State.EMPLOYED
                jobOffer.status = JobOffer.Status.CONSOLIDATED
                jobOffer.professional = professional
                jobOffer.calculateValue()
            }
            "DONE" -> {
                if(jobOffer.status != JobOffer.Status.CONSOLIDATED)
                    throw InvalidStatusException("Job offer status must be CONSOLIDATE to apply change")
                if (requestDTO.professionalId.isPresent)
                    throw InvalidStatusException("Id of professional is not required for status DONE")
                jobOffer.professional!!.employmentState = Professional.State.AVAILABLE_FOR_WORK
                jobOffer.status = JobOffer.Status.DONE
            }
            "ABORTED" -> {
                if(jobOffer.status == JobOffer.Status.DONE)
                    throw InvalidStatusException("Job offer status can't be DONE to apply change")
                if (requestDTO.professionalId.isPresent)
                    throw InvalidStatusException("Id of professional is not required for status ABORTED")
                jobOffer.professional!!.employmentState = Professional.State.AVAILABLE_FOR_WORK
                jobOffer.status = JobOffer.Status.ABORTED
            }
            else -> {
                throw InvalidStatusException("Invalid status")
            }
        }
        requestDTO.notes.ifPresent(){
            jobOffer.notes = requestDTO.notes.get()
        }
        return jobOffer.toDTO()
    }

    override fun getJobOfferValue(jobOfferId: Long): Float? {
        val jobOffer = jobOfferRepository.findById(jobOfferId).orElseThrow { JobOfferNotFoundException("Job offer with id $jobOfferId not found") }
        if(jobOffer.professional == null){
            throw ProfessionalNotAvailableException("No professional assigned")
        }
        return jobOffer.value
    }

    override fun getMessages(
        pageable: Pageable,
        sender: String?, subject: String?, channel: Channel?, state: State?,
        priority: Priority?, dateFrom: LocalDateTime?, dateTo: LocalDateTime?): List<MessageDTO> {
        var messages = messageRepository.findAll(pageable.sort).toMutableList()
        if(sender != null){
            messages = messages.filter { it.sender == sender }.toMutableList()
        }
        if(subject != null){
            messages = messages.filter { it.subject?.contains(subject, ignoreCase = true) == true }.toMutableList()
        }
        if(channel != null){
            messages = messages.filter { it.channel == channel }.toMutableList()
        }
        if(state != null){
            messages = messages.filter { it.getState() == state }.toMutableList()
        }
        if(priority != null){
            messages = messages.filter { it.priority == priority }.toMutableList()
        }
        if(dateFrom != null){
            messages = messages.filter { it.date >= dateFrom }.toMutableList()
        }
        if(dateTo != null){
            messages = messages.filter { it.date <= dateTo }.toMutableList()
        }
        return messages.take(pageable.pageSize).map(Message::toDTO)
    }

    override fun createMessage(createMessageDTO: CreateMessageDTO): MessageDTO {

        val message = Message(
            sender = createMessageDTO.sender,
            date = LocalDateTime.now(),
            subject = createMessageDTO.subject,
            body = createMessageDTO.body,
            channel = createMessageDTO.channel,
            state = State.RECEIVED,
            priority = createMessageDTO.priority ?: Priority.LOW
        )
        if(message.channel == Channel.EMAIL){
            val email = emailRepository.findByEmailContaining(message.sender).firstOrNull()
            if(email == null) {
                val newContact = CreateContactDTO("unknown", "unknown", emails = listOf(message.sender))
                createContact(newContact)
            }
        }else{
            val telephone = telephoneRepository.findByTelephoneContaining(message.sender).firstOrNull()
            if(telephone == null) {
                val newContact = CreateContactDTO("unknown", "unknown", telephones = listOf(message.sender))
                createContact(newContact)
            }
        }
        messageRepository.save(message)
        return message.toDTO()
    }

    override fun getMessage(id: Long): MessageDTO {
        val existingMessage = messageRepository.findById(id).orElseThrow {MessageNotFoundException("Message with id: $id not found")}
        return existingMessage.toDTO()
    }

    @Transactional
    override fun updateState(id: Long, updateMessageDTO: UpdateMessageDTO): MessageDTO{
        val existingMessage = messageRepository.findById(id).orElseThrow {MessageNotFoundException("Message with id: $id not found")}
        if (existingMessage.getState() == State.DISCARDED || existingMessage.getState() == State.FAILED || existingMessage.getState() == State.DONE)
            throw InvalidDataException("Message already done or discarded or failed")
        if(updateMessageDTO.state == State.RECEIVED)
            throw InvalidDataException("State cannot be set to RECEIVED")
        if(updateMessageDTO.state == State.PROCESSING && existingMessage.getState() != State.READ)
            throw InvalidDataException("State cannot be set to PROCESSING")
        if(updateMessageDTO.state == State.READ && existingMessage.getState() != State.RECEIVED)
            throw InvalidDataException("State cannot be set to READ")
        if(updateMessageDTO.state == State.DISCARDED && existingMessage.getState() != State.READ)
            throw InvalidDataException("State cannot be set to DISCARDED")
        if(updateMessageDTO.state == State.FAILED && (existingMessage.getState() != State.PROCESSING || existingMessage.getState() != State.READ))
            throw InvalidDataException("State cannot be set to FAILED")
        val history = History(date = LocalDateTime.now(), state = updateMessageDTO.state, comment = updateMessageDTO.comment, message = existingMessage)
        existingMessage.setState(updateMessageDTO.state)
        existingMessage.history.add(history)
        messageRepository.save(existingMessage)
        return existingMessage.toDTO()
    }

    override fun getMessageHistory(messageId: Long): List<MessageHistoryDTO> {
        val message =
            messageRepository.findById(messageId).orElseThrow { MessageNotFoundException("Message with id: $messageId not found")}
        val messageDTO = message.toDTO()
        return messageDTO.history
    }

    override fun updateMessagePriority(messageId: Long, newPriority: Priority): MessageDTO {
        val message = messageRepository.findById(messageId).orElseThrow { MessageNotFoundException("Message with id: $messageId not found")}
        logger.info("Updating message with id $messageId")
        message.priority = newPriority
        val updatedMessage = messageRepository.save(message)
        logger.info ("Message with id $messageId updated")
        val updatedMessageDTO = updatedMessage.toDTO()
        return updatedMessageDTO
    }

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
        //Duplicated Data handling
        if (createContactDTO.emails.size != createContactDTO.emails.toSet().size)
            throw DuplicatedDataException("You're sending duplicated emails!")
        if (createContactDTO.telephones.size != createContactDTO.telephones.toSet().size)
            throw DuplicatedDataException("You're sending duplicated telephone numbers!")
        if (createContactDTO.addresses.size != createContactDTO.addresses.toSet().size)
            throw DuplicatedDataException("You're sending duplicated addresses!")

        logger.info { "Creating contact ${createContactDTO.name} ${createContactDTO.surname}..." }
        val newContact = ContactDTO(
            name = createContactDTO.name,
            surname = createContactDTO.surname,
            ssn = createContactDTO.ssn,
            category = createContactDTO.category,
            emails = createContactDTO.emails.map { EmailDTO(email = it) }.toSet(),
            telephones = createContactDTO.telephones.map { TelephoneDTO(telephone = it) }.toSet(),
            addresses = createContactDTO.addresses.map { AddressDTO(address = it) }.toSet()
        ).toEntity()

        contactRepository.save(newContact)
        logger.info { "Contact: ${newContact.id} - ${newContact.name} ${newContact.surname} saved" }
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
        contact.emails = contactEmails
        logger.info { "Email updated!" }
        return contact.toDTO()
    }

    override fun deleteEmail(contactId: Long, emailId: Long): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException("Contact not found!") }
        contact.emails.find { it.id == emailId } ?: throw EmailNotFoundException("Email not found!")
        val filteredEmails = contact.emails.filter { it.id != emailId }.toSet()
        logger.info { "Deleting email with id: $emailId from contact: ${contact.id} ${contact.name} ${contact.surname}..." }
        contact.emails = filteredEmails
        emailRepository.deleteById(emailId)
        logger.info { "Email deleted!" }
        return contact.toDTO()
    }

    override fun updateCategory(contactId: Long, category: Category): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException("Contact not found!") }
        logger.info { "Updating Category..." }
        contact.category = category
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
        contact.telephones = contactTelephones
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
        contact.telephones = contactTelephones
        logger.info { "Telephone number updated!" }
        return contact.toDTO()
    }

    override fun deleteTelephone(contactId: Long, telephoneId: Long): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException("Contact not found!") }
        contact.telephones.firstOrNull { it.id == telephoneId } ?: throw TelephoneNotFoundException("Telephone number not found!")
        contact.telephones = contact.telephones.filter { it.id != telephoneId }.toSet()
        logger.info { "Deleting telephone number with id: $telephoneId from contact: ${contact.id} ${contact.name} ${contact.surname}..." }
        telephoneRepository.deleteById(telephoneId)
        logger.info { "Telephone number deleted!" }
        return contact.toDTO()
    }

    override fun addAddressToContact(contactId: Long, address: String): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow{ContactNotFoundException("Contact not found!")}
        val contactAddresses = contact.addresses.toMutableSet()
        if(contactAddresses.any { it.address == address })
            throw DuplicatedDataException("Address already present!")
        logger.info { "Creating address ${address}..." }
        contactAddresses.add(AddressDTO(address = address).toEntity(contact))
        logger.info { "Saving $address to contact: ${contact.id} ${contact.name} ${contact.surname}..." }
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
        contact.addresses = contactAddress
        logger.info { "Address updated!" }
        return contact.toDTO()
    }

    override fun deleteAddress(contactId: Long, addressId: Long): ContactDTO {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException("Contact not found!") }
        contact.addresses.find { it.id == addressId } ?: throw AddressNotFoundException("Address not found!")
        contact.addresses = contact.addresses.filter { it.id != addressId }.toSet()
        // No need of contactRepository.save(contact) because of "dirty checking" managing entities
        logger.info { "Deleting address with id: $addressId from contact: ${contact.id} ${contact.name} ${contact.surname}..." }
        addressRepository.deleteById(addressId)
        logger.info { "Address deleted!" }
        return contact.toDTO()
    }
}