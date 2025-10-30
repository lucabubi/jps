package it.polito.wa2.g19.crm.entities

import jakarta.persistence.Id
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.JoinColumn
import jakarta.persistence.GenerationType
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class Note(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var title: String,
    var createdAt: LocalDateTime,
    var description: String?,

    @ManyToOne
    @JoinColumn(name = "customer_id")
    var customer: Customer? = null,

    @ManyToOne
    @JoinColumn(name = "professional_id")
    var professional: Professional? = null,

    @ManyToOne
    @JoinColumn(name = "job_offer_id")
    var jobOffer: JobOffer? = null
)