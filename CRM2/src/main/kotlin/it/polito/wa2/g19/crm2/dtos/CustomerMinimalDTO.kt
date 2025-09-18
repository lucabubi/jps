package it.polito.wa2.g19.crm2.dtos

import it.polito.wa2.g19.crm2.entities.Customer

data class CustomerMinimalDTO(
    val id: Long = 0,
    val contact: ContactDTO,
    val notes: List<String> = emptyList()
){
    fun toEntity() : Customer {
        return Customer(
            id = this.id,
            contact = this.contact.toEntity(),
            notes = this.notes
        )
    }
}
