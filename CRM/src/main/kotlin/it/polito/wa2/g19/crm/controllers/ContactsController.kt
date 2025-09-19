package it.polito.wa2.g19.crm.controllers

import it.polito.wa2.g19.crm.dtos.ContactDTO
import it.polito.wa2.g19.crm.dtos.CreateContactDTO
import it.polito.wa2.g19.crm.dtos.UpdateContactDTO
import it.polito.wa2.g19.crm.entities.Category
import it.polito.wa2.g19.crm.exceptions.InvalidDataException
import it.polito.wa2.g19.crm.services.CRMService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val emailValidator = EmailValidator()
private val phonePattern = Regex("^\\+?[0-9]{10,12}$")
private val addressPattern = Regex("^[a-zA-Z0-9,\\-.\\s]+(\\s#\\d+)?$")

@RestController
@RequestMapping("/API/contacts")
class ContactsController(private val crmService: CRMService) {
    @GetMapping("/")
    fun getContacts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) surname: String?,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false) phoneNumber: String?,
        @RequestParam(defaultValue = "name") sortBy: String,
        @RequestParam(defaultValue = "asc") direction: String
    ): ResponseEntity<Any> {
        val sortDirection = if (direction == "asc") Sort.Direction.ASC else Sort.Direction.DESC
        val pageable: Pageable = try {
            if(sortBy != "name" && sortBy != "surname" && sortBy != "email" && sortBy != "phoneNumber")
                throw IllegalArgumentException()
            PageRequest.of(page, size, Sort.by(sortDirection, sortBy))
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Invalid paging/sorting parameter"))
        }
        val contacts = crmService.getContacts(pageable, name, surname, email, phoneNumber)
        return ResponseEntity.ok(contacts)
    }

    @GetMapping("/{contactId}")
    fun getContact(@PathVariable contactId: Long): ResponseEntity<ContactDTO> {
        val contact = crmService.getContact(contactId)
        return ResponseEntity.ok(contact)
    }

    @PostMapping("/")
    fun createContact(@Valid @RequestBody createContactDTO: CreateContactDTO) : ResponseEntity<ContactDTO> {
        if (!createContactDTO.emails.all { it.isNotBlank() && emailValidator.isValid(it, null) })
            throw InvalidDataException("Invalid email address!")
        if (!createContactDTO.telephones.all { phonePattern.matches(it) })
            throw InvalidDataException("Invalid telephone number!")
        if (!createContactDTO.addresses.all { addressPattern.matches(it) })
            throw InvalidDataException("Invalid address format!")

        val contactDTO = crmService.createContact(createContactDTO)
        return ResponseEntity.ok(contactDTO)
    }

    @PutMapping("/{contactId}")
    fun updateContact(@PathVariable contactId: Long, @Valid @RequestBody updateContactDTO: UpdateContactDTO): ResponseEntity<ContactDTO> {
        val updatedContact = crmService.updateContact(contactId, updateContactDTO)
        return ResponseEntity.ok(updatedContact)
    }

    @DeleteMapping("/{contactId}")
    fun deleteContact(@PathVariable contactId: Long): ResponseEntity<String> {
        crmService.deleteContact(contactId)
        return ResponseEntity.ok("Contact with id $contactId deleted")
    }

    @PostMapping("/{contactId}/email")
    fun addEmailToContact(@PathVariable contactId: Long, @Email @RequestBody email: String) : ResponseEntity<ContactDTO> {
        val contactDTO = crmService.addEmailToContact(contactId,email)
        return ResponseEntity.ok(contactDTO)
    }

    @PutMapping("{contactId}/email/{emailId}")
    fun updateEmail(@PathVariable contactId: Long, @PathVariable emailId: Long, @Email @RequestBody email: String) : ResponseEntity<ContactDTO> {
        val contactDTO = crmService.updateEmail(contactId, emailId, email)
        return ResponseEntity.ok(contactDTO)
    }

    @DeleteMapping("{contactId}/email/{emailId}")
    fun deleteEmailFromContact(@PathVariable contactId: Long, @PathVariable emailId:  Long) : ResponseEntity<ContactDTO> {
        val contactDTO = crmService.deleteEmail(contactId, emailId)
        return ResponseEntity.ok(contactDTO)
    }

    @PutMapping("{contactId}/category")
    fun updateCategory(@PathVariable contactId: Long, @RequestBody category: Category) : ResponseEntity<ContactDTO> {
        val contactDTO = crmService.updateCategory(contactId, category)
        return ResponseEntity.ok(contactDTO)
    }

    @PostMapping("{contactId}/telephone")
    fun addTelephoneToContact(@PathVariable contactId: Long, @RequestBody telephone: String) : ResponseEntity<ContactDTO> {
        if (!phonePattern.matches(telephone))
            throw InvalidDataException("Invalid telephone number!")
        val contactDTO = crmService.addTelephoneToContact(contactId, telephone)
        return ResponseEntity.ok(contactDTO)
    }

    @PutMapping("{contactId}/telephone/{telephoneId}")
    fun updateTelephone(@PathVariable contactId: Long, @PathVariable telephoneId: Long, @RequestBody telephone: String) : ResponseEntity<ContactDTO> {
        if (!phonePattern.matches(telephone))
            throw InvalidDataException("Invalid telephone number!")
        val contactDTO = crmService.updateTelephone(contactId, telephoneId, telephone)
        return ResponseEntity.ok(contactDTO)
    }

    @DeleteMapping("{contactId}/telephone/{telephoneId}")
    fun deleteTelephone(@PathVariable contactId: Long, @PathVariable telephoneId: Long) : ResponseEntity<ContactDTO> {
        val contactDTO = crmService.deleteTelephone(contactId, telephoneId)
        return ResponseEntity.ok(contactDTO)
    }

    @PostMapping("{contactId}/address")
    fun addAddressToContact(@PathVariable contactId: Long, @RequestBody address: String) : ResponseEntity<ContactDTO> {
        if (!addressPattern.matches(address))
            throw InvalidDataException("Invalid address format")
        val contactDTO = crmService.addAddressToContact(contactId, address)
        return ResponseEntity.ok(contactDTO)
    }

    @DeleteMapping("{contactId}/address/{addressId}")
    fun deleteAddressFromContact(@PathVariable contactId: Long, @PathVariable addressId: Long) : ResponseEntity<ContactDTO> {
        val contactDTO = crmService.deleteAddress(contactId, addressId)
        return ResponseEntity.ok(contactDTO)
    }

    @PutMapping("{contactId}/address/{addressId}")
    fun updateAddress(@PathVariable contactId: Long, @PathVariable addressId: Long, @RequestBody address: String) : ResponseEntity<ContactDTO> {
        if (!addressPattern.matches(address))
            throw InvalidDataException("Invalid address format")
        val contactDTO = crmService.updateAddress(contactId, addressId, address)
        return ResponseEntity.ok(contactDTO)
    }
}