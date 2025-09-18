package it.polito.wa2.g19.crm2.entities

import it.polito.wa2.g19.crm2.dtos.TelephoneDTO
import jakarta.persistence.*

@Entity
class Telephone(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var telephone: String,
    @ManyToOne
    @JoinColumn(name = "contact_id")
    var contact: Contact
) {
    fun toDTO() = TelephoneDTO(
        id = this.id,
        telephone = this.telephone
    )
}