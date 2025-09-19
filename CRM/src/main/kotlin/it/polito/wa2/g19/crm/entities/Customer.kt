package it.polito.wa2.g19.crm.entities

import it.polito.wa2.g19.crm.dtos.CustomerDTO
import jakarta.persistence.*

@Entity
class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "contact_id", referencedColumnName = "id")
    var contact: Contact,
    @ElementCollection
    @Column(length = 1000)
    var notes: List<String> = emptyList(),
    @OneToMany(mappedBy = "customer")
    var jobOffers: Set<JobOffer> = emptySet()
){
    fun toDTO() = CustomerDTO(
        id = this.id,
        contact = this.contact.toDTO(),
        notes = this.notes,
        jobOffers = this.jobOffers.map { it.toDTO() }.toSet()
    )
}