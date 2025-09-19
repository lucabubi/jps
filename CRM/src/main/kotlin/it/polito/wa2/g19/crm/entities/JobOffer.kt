package it.polito.wa2.g19.crm.entities

import it.polito.wa2.g19.crm.dtos.CustomerMinimalDTO
import it.polito.wa2.g19.crm.dtos.JobOfferDTO
import jakarta.persistence.*

// Global fixed value for profit margin
const val profit_margin: Float = 0.2f

@Entity
class JobOffer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var description: String = "",
    var status: Status = Status.CREATED,
    var duration: Int = 0,
    @ElementCollection
    @Column(length = 1000)
    var notes: List<String> = emptyList(),
    @ElementCollection
    var requiredSkills: Set<String> = emptySet(),
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "customer_id")
    var customer: Customer,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "professional_id")
    var professional: Professional? = null,
    var value: Float? = null
) {
    fun toDTO() : JobOfferDTO {
        return JobOfferDTO(
            id = this.id,
            description = this.description,
            status = this.status,
            duration = this.duration,
            notes = this.notes,
            requiredSkills = this.requiredSkills,
            customer = CustomerMinimalDTO(
                id = this.customer.id,
                contact = this.customer.contact.toDTO(),
                notes = this.customer.notes
            ),
            professional = this.professional?.toDTO(),
            value = this.value
        )
    }

    enum class Status {
        CREATED,
        SELECTION_PHASE,
        CANDIDATE_PROPOSAL,
        CONSOLIDATED,
        DONE,
        ABORTED
    }
    fun calculateValue() {
        this.value = professional?.let { duration * profit_margin * it.dailyRate }
    }

}