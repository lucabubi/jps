package it.polito.wa2.g19.crm2.entities

import it.polito.wa2.g19.crm2.dtos.EmailDTO
import jakarta.persistence.*

@Entity
class Email(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var email: String,
    @ManyToOne
    @JoinColumn(name = "contact_id")
    var contact: Contact
) {
    fun toDTO() = EmailDTO(
        id = this.id,
        email = this.email
    )
}