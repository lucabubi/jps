package it.polito.wa2.g19.crm2.dtos

import it.polito.wa2.g19.crm2.entities.Contact
import it.polito.wa2.g19.crm2.entities.Telephone

data class TelephoneDTO(
    val id: Long = 0,
    val telephone: String
){
    fun toEntity(contact: Contact) = Telephone(
        id = this.id,
        telephone = this.telephone,
        contact = contact
    )
}