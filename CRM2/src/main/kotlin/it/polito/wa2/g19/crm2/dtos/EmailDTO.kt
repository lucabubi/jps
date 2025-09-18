package it.polito.wa2.g19.crm2.dtos

import it.polito.wa2.g19.crm2.entities.Contact
import it.polito.wa2.g19.crm2.entities.Email

data class EmailDTO(
    val id: Long = 0,
    val email: String
){
    fun toEntity(contact: Contact) = Email(
        id = this.id,
        email = this.email,
        contact = contact
    )
}

