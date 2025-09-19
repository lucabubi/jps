package it.polito.wa2.g19.crm.dtos

import it.polito.wa2.g19.crm.entities.Address
import it.polito.wa2.g19.crm.entities.Contact

data class AddressDTO(
    val id: Long = 0,
    val address: String
){
    fun toEntity(contact: Contact) = Address(
        id = this.id,
        address = this.address,
        contact = contact
    )
}