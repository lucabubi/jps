package it.polito.wa2.g19.crm2.dtos

import it.polito.wa2.g19.crm2.entities.Address
import it.polito.wa2.g19.crm2.entities.Contact

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