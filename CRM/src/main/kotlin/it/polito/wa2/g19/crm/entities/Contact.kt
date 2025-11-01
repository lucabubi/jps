package it.polito.wa2.g19.crm.entities

import it.polito.wa2.g19.crm.dtos.ContactDTO
import jakarta.persistence.*

@Entity
class Contact (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: String,
    var surname: String,
    var ssn: String? = null,
    var region: Region,
    var category: Category = Category.UNKNOWN,
    @OneToMany(mappedBy = "contact", cascade = [CascadeType.ALL])
    var notes: MutableSet<Note> = mutableSetOf(),
    @OneToMany(mappedBy = "contact", cascade = [CascadeType.ALL])
    var emails: MutableSet<Email> = mutableSetOf(),
    @OneToMany(mappedBy = "contact", cascade = [CascadeType.ALL])
    var addresses: MutableSet<Address> = mutableSetOf(),
    @OneToMany(mappedBy = "contact", cascade = [CascadeType.ALL])
    var telephones: MutableSet<Telephone> = mutableSetOf()
){

    fun toDTO() = ContactDTO(
        id = this.id,
        name = this.name,
        surname = this.surname,
        ssn = this.ssn,
        region = this.region,
        category = this.category,
        notes = this.notes.map { it.toDTO() }.toSet(),
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

enum class Region {
    EMEA,
    NA,
    LATAM,
    APAC,
    UNKNOWN
}