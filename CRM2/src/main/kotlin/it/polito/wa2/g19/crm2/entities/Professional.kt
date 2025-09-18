package it.polito.wa2.g19.crm2.entities

import it.polito.wa2.g19.crm2.dtos.ProfessionalDTO
import jakarta.persistence.*

@Entity
class Professional (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "contact_id", referencedColumnName = "id")
    var contact: Contact,
    @ElementCollection
    @Column(length = 1000)
    var notes: List<String> = emptyList(),
    @ElementCollection
    var skills: Set<String> = emptySet(),
    var dailyRate: Float = 0.0f,
    @OneToMany(mappedBy = "professional", cascade = [CascadeType.ALL])
    var jobOffers: Set<JobOffer> = emptySet(),
    var employmentState: State = State.AVAILABLE_FOR_WORK,
    var location: String? = null
) {
    fun toDTO() = ProfessionalDTO(
        id = this.id,
        contact = this.contact.toDTO(),
        notes = this.notes,
        skills = this.skills,
        dailyRate = this.dailyRate,
        employmentState = this.employmentState,
        location = this.location
    )

    enum class State {
        EMPLOYED,
        AVAILABLE_FOR_WORK,
        NOT_AVAILABLE
    }
}