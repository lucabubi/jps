package it.polito.wa2.g19.crm2.dtos

import it.polito.wa2.g19.crm2.entities.Category
import it.polito.wa2.g19.crm2.entities.Contact

data class ContactDTO (
    val id: Long = 0,
    val name: String,
    val surname: String,
    val ssn: String? = null,
    val category: Category = Category.UNKNOWN,
    val emails: Set<EmailDTO> = emptySet(),
    val addresses: Set<AddressDTO> = emptySet(),
    val telephones: Set<TelephoneDTO> = emptySet()
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContactDTO) return false
        if (name != other.name) return false
        if (surname != other.surname) return false
        if (ssn != other.ssn) return false
        if (category != other.category) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + surname.hashCode()
        result = 31 * result + (ssn?.hashCode() ?: 0)
        result = 31 * result + category.hashCode()
        return result
    }

    fun toEntity(): Contact {
        val newContact = Contact(
            id = this.id,
            name = this.name,
            surname = this.surname,
            ssn = this.ssn,
            category = this.category
        )
        newContact.emails = this.emails.map { it.toEntity(newContact) }.toSet()
        newContact.telephones = this.telephones.map { it.toEntity(newContact) }.toSet()
        newContact.addresses = this.addresses.map { it.toEntity(newContact) }.toSet()
        return newContact
    }
}

