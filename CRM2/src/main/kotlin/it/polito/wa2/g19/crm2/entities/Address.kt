package it.polito.wa2.g19.crm2.entities

import it.polito.wa2.g19.crm2.dtos.AddressDTO
import jakarta.persistence.*


@Entity
class Address (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var address: String,
    @ManyToOne
    @JoinColumn(name = "contact_id")
    var contact: Contact
){
    fun toDTO() = AddressDTO(
        id = this.id,
        address = this.address
    )
}