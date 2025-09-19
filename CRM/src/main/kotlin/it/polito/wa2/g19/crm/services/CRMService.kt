package it.polito.wa2.g19.crm.services

import it.polito.wa2.g19.crm.dtos.*
import it.polito.wa2.g19.crm.entities.*
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.util.*

interface CRMService {
    fun createCustomer(customerDTO: CustomerDTO): CustomerDTO
    fun getCustomers(): List<CustomerDTO>
    fun getCustomer(customerId: Long): CustomerDTO
    fun updateCustomerNotes(id: Long, notes: List<String>) : CustomerDTO
    fun createProfessional(professionalDTO: ProfessionalDTO): ProfessionalDTO
    fun getProfessionals(
        pageable: Pageable,
        employmentState: Optional<Professional.State>,
        location: Optional<String>,
        skills: Optional<List<String>>
    ) : List<ProfessionalDTO>
    fun getProfessional(professionalId: Long): ProfessionalDTO
    fun updateProfessional(id: Long, updateDTO: ProfessionalUpdateDTO) : ProfessionalDTO
    fun createJobOffer(createJobOfferDTO: CreateJobOfferDTO): JobOfferDTO
    fun getJobOffers(pageable: Pageable, customerId: Long?, status: JobOffer.Status?, professionalId: Long?): List<JobOfferDTO>
    fun getOpenJobOffers(customerId: Long, pageable: Pageable): List<JobOfferDTO>
    fun getAcceptedJobOffers(professionalId: Long, pageable: Pageable): List<JobOfferDTO>
    fun getAbortedJobOffers(pageable: Pageable, customerId: Long?, professionalId: Long?): List<JobOfferDTO>
    fun updateJobOffer(jobOfferId: Long, requestDTO: JobOfferUpdateDTO): JobOfferDTO
    fun getJobOfferValue(jobOfferId: Long): Float?
    fun getMessages(
        pageable: Pageable, sender: String?, subject: String?, channel: Channel?, state: State?,
        priority: Priority?, dateFrom: LocalDateTime?, dateTo: LocalDateTime?): List<MessageDTO>
    fun createMessage(createMessageDTO: CreateMessageDTO): MessageDTO
    fun getMessage(id: Long): MessageDTO
    fun updateState(id: Long, updateMessageDTO: UpdateMessageDTO): MessageDTO
    fun getMessageHistory(messageId: Long): List<MessageHistoryDTO>
    fun updateMessagePriority(messageId: Long, newPriority: Priority): MessageDTO
    fun getContacts(pageable: Pageable, name: String?, surname: String?, email: String?, phoneNumber: String?): List<ContactDTO>
    fun getContact(contactId: Long): ContactDTO
    fun createContact(createContactDTO: CreateContactDTO): ContactDTO
    fun updateContact(contactId: Long, updateContactDTO: UpdateContactDTO): ContactDTO
    fun deleteContact(contactId: Long)
    fun addEmailToContact(contactId: Long, email: String): ContactDTO
    fun updateEmail(contactId: Long, emailId: Long, email: String): ContactDTO
    fun deleteEmail(contactId: Long, emailId: Long): ContactDTO
    fun updateCategory(contactId: Long, category: Category): ContactDTO
    fun addTelephoneToContact(contactId: Long, telephone: String): ContactDTO
    fun updateTelephone(contactId: Long, telephoneId: Long, telephone: String): ContactDTO
    fun deleteTelephone(contactId: Long, telephoneId: Long): ContactDTO
    fun addAddressToContact(contactId: Long, address: String): ContactDTO
    fun updateAddress(contactId: Long, addressId: Long, address: String): ContactDTO
    fun deleteAddress(contactId: Long, addressId: Long): ContactDTO
}