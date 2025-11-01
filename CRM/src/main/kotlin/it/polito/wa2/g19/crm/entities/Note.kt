package it.polito.wa2.g19.crm.entities

import it.polito.wa2.g19.crm.dtos.NoteDTO
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
    @JoinColumn(name = "contact_id")
    var contact: Contact? = null,

    @ManyToOne
    @JoinColumn(name = "job_offer_id")
    var jobOffer: JobOffer? = null
) {

    fun toDTO() = NoteDTO(
        id = this.id,
        title = this.title,
        createdAt = this.createdAt,
        description = this.description,
    )
}