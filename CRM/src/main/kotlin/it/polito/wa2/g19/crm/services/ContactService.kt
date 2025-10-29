package it.polito.wa2.g19.crm.services

import it.polito.wa2.g19.crm.dtos.ContactDTO
import it.polito.wa2.g19.crm.dtos.CreateContactDTO
import it.polito.wa2.g19.crm.dtos.UpdateContactDTO
import it.polito.wa2.g19.crm.dtos.AddressDTO
import it.polito.wa2.g19.crm.entities.Category
import org.springframework.data.domain.Pageable

interface ContactService {
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
    fun addAddressToContact(contactId: Long, address: AddressDTO): ContactDTO
    fun updateAddress(contactId: Long, addressId: Long, address: String): ContactDTO
    fun deleteAddress(contactId: Long, addressId: Long): ContactDTO
}