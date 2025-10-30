package it.polito.wa2.g19.crm.entities

import it.polito.wa2.g19.crm.dtos.CustomerDTO
import it.polito.wa2.g19.crm.dtos.toDTO
import jakarta.persistence.*

@Entity
class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "contact_id", referencedColumnName = "id")
    var contact: Contact,
    @OneToMany(mappedBy = "customer", cascade = [CascadeType.ALL])
    var notes: MutableSet<Note> = mutableSetOf(),
    @OneToMany(mappedBy = "customer")
    var jobOffers: MutableSet<JobOffer> = mutableSetOf()
){
    fun toDTO() = CustomerDTO(
        id = this.id,
        contact = this.contact.toDTO(),
        notes = this.notes.map { it.toDTO() },
        jobOffers = this.jobOffers.map { it.toDTO() }.toSet()
    )
}