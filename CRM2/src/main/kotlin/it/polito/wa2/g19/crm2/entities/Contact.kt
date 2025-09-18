package it.polito.wa2.g19.crm2.entities

import it.polito.wa2.g19.crm2.dtos.ContactDTO
import jakarta.persistence.*

@Entity
class Contact (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: String,
    var surname: String,
    var ssn: String? = null,
    var category: Category = Category.UNKNOWN,
    @OneToMany(mappedBy = "contact", cascade = [CascadeType.ALL])
    var emails: Set<Email> = emptySet(),
    @OneToMany(mappedBy = "contact", cascade = [CascadeType.ALL])
    var addresses: Set<Address> = emptySet(),
    @OneToMany(mappedBy = "contact", cascade = [CascadeType.ALL])
    var telephones: Set<Telephone> = emptySet()
){
    fun toDTO() = ContactDTO(
        id = this.id,
        name = this.name,
        surname = this.surname,
        ssn = this.ssn,
        category = this.category,
        emails = this.emails.map { it.toDTO() }.toSet(),
        addresses = this.addresses.map { it.toDTO() }.toSet(),
        telephones = this.telephones.map { it.toDTO() }.toSet()
    )
}

enum class Category {
    CUSTOMER,
    PROFESSIONAL,
    UNKNOWN
}