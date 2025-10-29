package it.polito.wa2.g19.crm.dtos

import it.polito.wa2.g19.crm.entities.Address
import it.polito.wa2.g19.crm.entities.Contact

data class AddressDTO(
    val id: Long = 0,
    val address: String,
    val zipCode: String,
    val city: String,
    val country: String,
){
    fun toEntity(contact: Contact) = Address(
        id = this.id,
        address = this.address,
        zipCode = this.zipCode,
        city = this.city,
        country = this.country,
        contact = contact
    )
}