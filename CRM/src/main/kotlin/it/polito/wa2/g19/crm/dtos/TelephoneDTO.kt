package it.polito.wa2.g19.crm.dtos

import it.polito.wa2.g19.crm.entities.Contact
import it.polito.wa2.g19.crm.entities.Telephone

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