package it.polito.wa2.g19.crm.dtos

import it.polito.wa2.g19.crm.entities.Customer

data class CustomerDTO(
    val id: Long = 0,
    val contact: ContactDTO,
    val notes: List<String> = emptyList(),
    val jobOffers: Set<JobOfferDTO> = emptySet()
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CustomerDTO) return false
        if (id != other.id) return false
        if (contact != other.contact) return false
        if (notes != other.notes) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + contact.hashCode()
        result = 31 * result + notes.hashCode()
        return result
    }

    fun toEntity() : Customer {
        return Customer(
            id = this.id,
            contact = this.contact.toEntity(),
            notes = this.notes,
            jobOffers = this.jobOffers.map { it.toEntity() }.toSet()
        )
    }
}