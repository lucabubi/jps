package it.polito.wa2.g19.crm2.controllers

import it.polito.wa2.g19.crm2.dtos.*
import it.polito.wa2.g19.crm2.entities.*
import it.polito.wa2.g19.crm2.exceptions.InvalidDataException
import it.polito.wa2.g19.crm2.services.CRM2Service
import org.springframework.data.domain.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator
import java.time.LocalDateTime
import java.util.*

private val emailValidator = EmailValidator()
private val phonePattern = Regex("^\\+?[0-9]{10,12}$")
private val addressPattern = Regex("^[a-zA-Z0-9,\\-.\\s]+(\\s#\\d+)?$")

@RestController
@RequestMapping("/API/customers")
class CRM2ControllerCustomers(private val crm2Service: CRM2Service) {
    @PostMapping("/")
    fun createCustomer(@RequestBody customer: CustomerDTO): ResponseEntity<CustomerDTO> {
        val createdCustomer = crm2Service.createCustomer(customer)
        return ResponseEntity.ok(createdCustomer)
    }

    @GetMapping("/")
    fun getCustomers(): ResponseEntity<List<CustomerDTO>> {
        val customers = crm2Service.getCustomers()
        return ResponseEntity.ok(customers)
    }

    @GetMapping("/{customerId}")
    fun getCustomer(@PathVariable customerId: Long): ResponseEntity<CustomerDTO> {
        val customer = crm2Service.getCustomer(customerId)
        return ResponseEntity.ok(customer)
    }

    @PutMapping("/{id}/notes")
    fun updateCustomerNotes(@PathVariable id: Long, @RequestBody notes: List<String>) : ResponseEntity<CustomerDTO>{
        val customerDTO = crm2Service.updateCustomerNotes(id, notes)
        return ResponseEntity.ok(customerDTO)
    }
}

@RestController
@RequestMapping("/API/professionals")
class CRM2ControllerProfessionals(private val crm2Service: CRM2Service) {
    @PostMapping("/")
    fun createProfessional(@RequestBody professional: ProfessionalDTO): ResponseEntity<ProfessionalDTO> {
        val createdProfessional = crm2Service.createProfessional(professional)
        return ResponseEntity.ok(createdProfessional)
    }

    @GetMapping("/")
    fun getProfessionals(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) employmentState: Optional<Professional.State>,
        @RequestParam(required = false) location: Optional<String>,
        @RequestParam(required = false) skills: Optional<List<String>>
    ) : ResponseEntity<List<ProfessionalDTO>>{
        val pageable = PageRequest.of(page, size)
        val professionalsDTO = crm2Service.getProfessionals(pageable, employmentState, location, skills)
        return ResponseEntity.ok(professionalsDTO)
    }

    @GetMapping("/{professionalId}")
    fun getProfessional(@PathVariable professionalId: Long): ResponseEntity<ProfessionalDTO> {
        val professional = crm2Service.getProfessional(professionalId)
        return ResponseEntity.ok(professional)
    }

    @PutMapping("/{id}")
    fun updateProfessional(@PathVariable id: Long, @RequestBody updateDTO: ProfessionalUpdateDTO) : ResponseEntity<ProfessionalDTO>{
        val professionalDTO = crm2Service.updateProfessional(id, updateDTO)
        return ResponseEntity.ok(professionalDTO)
    }
}

@RestController
@RequestMapping("/API/joboffers")
class CRM2ControllerJobOffers( private val crm2Service: CRM2Service) {

    @PostMapping("/")
    fun createJobOffers(@RequestBody createJobOfferDTO: CreateJobOfferDTO) : ResponseEntity<JobOfferDTO>{
        val newJobOffer = crm2Service.createJobOffer(createJobOfferDTO)
        return ResponseEntity.ok(newJobOffer)
    }

    @GetMapping("/")
    fun getJobOffers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) customerId: Long?,
        @RequestParam(required = false) status: JobOffer.Status?,
        @RequestParam(required = false) professionalId: Long?
    ) : ResponseEntity<List<JobOfferDTO>>{
        val pageable = PageRequest.of(page, size)
        val jobOffers = crm2Service.getJobOffers(pageable, customerId, status, professionalId)
        return ResponseEntity.ok(jobOffers)
    }

    @GetMapping("/open/{customerId}")
    fun getOpenJobOffers(
        @PathVariable customerId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int) : ResponseEntity<Any> {
        val pageable: Pageable = try {
            PageRequest.of(page, size)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body("Invalid page or size")
        }
        val openJobOffers = crm2Service.getOpenJobOffers(customerId, pageable)
        return ResponseEntity.ok(openJobOffers)
    }

    @GetMapping("/accepted/{professionalId}")
    fun getAcceptedJobOffers(
        @PathVariable professionalId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int) : ResponseEntity<Any> {
        val pageable: Pageable = try {
            PageRequest.of(page, size)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body("Invalid page or size")
        }
        val acceptedJobOffers = crm2Service.getAcceptedJobOffers(professionalId, pageable)
        return ResponseEntity.ok(acceptedJobOffers)
    }

    @GetMapping("/aborted/")
    fun getAbortedJobOffers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) customerId: Long?,
        @RequestParam(required = false) professionalId: Long?) : ResponseEntity<Any> {
        val pageable: Pageable = try {
            PageRequest.of(page, size)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body("Invalid page or size")
        }
        val abortedJobOffers = crm2Service.getAbortedJobOffers(pageable, customerId, professionalId)
        return ResponseEntity.ok(abortedJobOffers)
    }

    @PostMapping("/{jobOfferId}")
    fun changeJobOfferStatus(@PathVariable jobOfferId: Long, @RequestBody requestDTO: JobOfferUpdateDTO) : ResponseEntity<JobOfferDTO> {
        val updateJobOffer = crm2Service.updateJobOffer(jobOfferId, requestDTO)
        return ResponseEntity.ok(updateJobOffer)
    }

    @GetMapping("/{jobOfferId}/value")
    fun getJobOfferValue(@PathVariable jobOfferId: Long) : ResponseEntity<Float>{
        val jobOffersValue = crm2Service.getJobOfferValue(jobOfferId)
        return ResponseEntity.ok(jobOffersValue)
    }
}

@RestController
@RequestMapping("/API/messages")
class CRMControllerMessages(private val crm2Service: CRM2Service) {

    @GetMapping("/")
    fun getMessages(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) sender: String?,
        @RequestParam(required = false) subject: String?,
        @RequestParam(required = false) channel: Channel?,
        @RequestParam(required = false) state: State?,
        @RequestParam(required = false) priority: Priority?,
        @RequestParam(required = false) from: LocalDateTime?,
        @RequestParam(required = false) to: LocalDateTime?,
        @RequestParam(defaultValue = "date") sortBy: String,
        @RequestParam(defaultValue = "asc") direction: String
    ): ResponseEntity<Any> {

        val sortDirection = if (direction == "asc") Sort.Direction.ASC else Sort.Direction.DESC
        val pageable: Pageable = try {
            if(sortBy != "sender" && sortBy != "subject" && sortBy != "channel" && sortBy != "state" && sortBy != "priority" && sortBy != "date")
                throw IllegalArgumentException()
            PageRequest.of(page, size, Sort.by(sortDirection, sortBy))
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Invalid paging/sorting parameter"))
        }
        val messages = crm2Service.getMessages(pageable, sender, subject, channel, state, priority, from, to)

        return ResponseEntity.ok(messages)
    }

    @PostMapping("/")
    fun createMessage(@Valid @RequestBody createMessageDTO: CreateMessageDTO): ResponseEntity<MessageDTO>{
        if (createMessageDTO.channel == Channel.EMAIL && !emailValidator.isValid(createMessageDTO.sender, null))
            throw InvalidDataException("Invalid sender email!")
        if ((createMessageDTO.channel == Channel.PHONE_CALL || createMessageDTO.channel == Channel.TEXT_MESSAGE)
            && !phonePattern.matches(createMessageDTO.sender))
            throw InvalidDataException("Invalid sender phone number!")

        val message = crm2Service.createMessage(createMessageDTO)
        return ResponseEntity.ok(message)
    }

    @GetMapping("/{id}")
    fun getMessage(@PathVariable id: Long) : ResponseEntity<MessageDTO>{
        val message = crm2Service.getMessage(id)
        return ResponseEntity.ok(message)
    }

    @PostMapping("/{id}")
    fun updateMessageState(
        @PathVariable id: Long,
        @RequestBody updateMessageDTO: UpdateMessageDTO) : ResponseEntity<String> {
        crm2Service.updateState(id, updateMessageDTO)
        return ResponseEntity.ok("Message with id $id updated")
    }

    @GetMapping("/{messageId}/history")
    fun getMessageHistory(@PathVariable messageId: Long): ResponseEntity<List<MessageHistoryDTO>> {
        return ResponseEntity.ok(crm2Service.getMessageHistory(messageId))
    }

    @PutMapping("/{messageId}/priority")
    fun updateMessagePriority(@PathVariable messageId: Long, @RequestBody priority: String): ResponseEntity<Any> {
        val newPriority = try {Priority.valueOf(priority)}
        catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Invalid priority value"))
        }
        val updatedMessage = crm2Service.updateMessagePriority(messageId, newPriority)
        return ResponseEntity.ok(updatedMessage)
    }
}

@RestController
@RequestMapping("/API/contacts")
class CRMControllerContacts(private val crm2Service: CRM2Service) {
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
        val contacts = crm2Service.getContacts(pageable, name, surname, email, phoneNumber)
        return ResponseEntity.ok(contacts)
    }

    @GetMapping("/{contactId}")
    fun getContact(@PathVariable contactId: Long): ResponseEntity<ContactDTO> {
        val contact = crm2Service.getContact(contactId)
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

        val contactDTO = crm2Service.createContact(createContactDTO)
        return ResponseEntity.ok(contactDTO)
    }

    @PutMapping("/{contactId}")
    fun updateContact(@PathVariable contactId: Long, @Valid @RequestBody updateContactDTO: UpdateContactDTO): ResponseEntity<ContactDTO> {
        val updatedContact = crm2Service.updateContact(contactId, updateContactDTO)
        return ResponseEntity.ok(updatedContact)
    }

    @DeleteMapping("/{contactId}")
    fun deleteContact(@PathVariable contactId: Long): ResponseEntity<String> {
        crm2Service.deleteContact(contactId)
        return ResponseEntity.ok("Contact with id $contactId deleted")
    }

    @PostMapping("/{contactId}/email")
    fun addEmailToContact(@PathVariable contactId: Long, @Email @RequestBody email: String) : ResponseEntity<ContactDTO> {
        val contactDTO = crm2Service.addEmailToContact(contactId,email)
        return ResponseEntity.ok(contactDTO)
    }

    @PutMapping("{contactId}/email/{emailId}")
    fun updateEmail(@PathVariable contactId: Long, @PathVariable emailId: Long, @Email @RequestBody email: String) : ResponseEntity<ContactDTO> {
        val contactDTO = crm2Service.updateEmail(contactId, emailId, email)
        return ResponseEntity.ok(contactDTO)
    }

    @DeleteMapping("{contactId}/email/{emailId}")
    fun deleteEmailFromContact(@PathVariable contactId: Long, @PathVariable emailId:  Long) : ResponseEntity<ContactDTO> {
        val contactDTO = crm2Service.deleteEmail(contactId, emailId)
        return ResponseEntity.ok(contactDTO)
    }

    @PutMapping("{contactId}/category")
    fun updateCategory(@PathVariable contactId: Long, @RequestBody category: Category) : ResponseEntity<ContactDTO> {
        val contactDTO = crm2Service.updateCategory(contactId, category)
        return ResponseEntity.ok(contactDTO)
    }

    @PostMapping("{contactId}/telephone")
    fun addTelephoneToContact(@PathVariable contactId: Long, @RequestBody telephone: String) : ResponseEntity<ContactDTO> {
        if (!phonePattern.matches(telephone))
            throw InvalidDataException("Invalid telephone number!")
        val contactDTO = crm2Service.addTelephoneToContact(contactId, telephone)
        return ResponseEntity.ok(contactDTO)
    }

    @PutMapping("{contactId}/telephone/{telephoneId}")
    fun updateTelephone(@PathVariable contactId: Long, @PathVariable telephoneId: Long, @RequestBody telephone: String) : ResponseEntity<ContactDTO> {
        if (!phonePattern.matches(telephone))
            throw InvalidDataException("Invalid telephone number!")
        val contactDTO = crm2Service.updateTelephone(contactId, telephoneId, telephone)
        return ResponseEntity.ok(contactDTO)
    }

    @DeleteMapping("{contactId}/telephone/{telephoneId}")
    fun deleteTelephone(@PathVariable contactId: Long, @PathVariable telephoneId: Long) : ResponseEntity<ContactDTO> {
        val contactDTO = crm2Service.deleteTelephone(contactId, telephoneId)
        return ResponseEntity.ok(contactDTO)
    }

    @PostMapping("{contactId}/address")
    fun addAddressToContact(@PathVariable contactId: Long, @RequestBody address: String) : ResponseEntity<ContactDTO> {
        if (!addressPattern.matches(address))
            throw InvalidDataException("Invalid address format")
        val contactDTO = crm2Service.addAddressToContact(contactId, address)
        return ResponseEntity.ok(contactDTO)
    }

    @DeleteMapping("{contactId}/address/{addressId}")
    fun deleteAddressFromContact(@PathVariable contactId: Long, @PathVariable addressId: Long) : ResponseEntity<ContactDTO> {
        val contactDTO = crm2Service.deleteAddress(contactId, addressId)
        return ResponseEntity.ok(contactDTO)
    }

    @PutMapping("{contactId}/address/{addressId}")
    fun updateAddress(@PathVariable contactId: Long, @PathVariable addressId: Long, @RequestBody address: String) : ResponseEntity<ContactDTO> {
        if (!addressPattern.matches(address))
            throw InvalidDataException("Invalid address format")
        val contactDTO = crm2Service.updateAddress(contactId, addressId, address)
        return ResponseEntity.ok(contactDTO)
    }
}