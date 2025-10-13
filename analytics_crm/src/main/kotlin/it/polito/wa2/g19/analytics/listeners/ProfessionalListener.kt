package it.polito.wa2.g19.analytics.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.polito.wa2.g19.analytics.models.ProfessionalDocument
import it.polito.wa2.g19.analytics.repositories.ProfessionalRepository
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class ProfessionalListener(
    private val repo: ProfessionalRepository,
    private val om: ObjectMapper
) {

    data class ProfessionalEvent(
        val id: Long,
        val fullName: String? = null,
        val email: String? = null,
        val phone: String? = null,
        val skills: Set<String>? = null,
        val rating: Float? = null,
        val location: String? = null,
        val dailyRate: Float? = null,
        val employmentState: String? = null,
        val notes: List<String>? = null,
        val eventType: String,
        val changedFields: Set<String>? = null,
        val occurredAt: Long? = null
    )

    @KafkaListener(
        topics = ["professionalTopic"],
        groupId = "analytics-crm"
    )
    fun onMessage(value: String) {
        val evt = om.readValue<ProfessionalEvent>(value)
        when (evt.eventType.uppercase()) {
            "CREATED", "UPDATED", "UPSERT" -> upsert(evt)
            "DELETED" -> delete(evt.id)
            else -> logger.warn { "Evento professional ignorato: type=${evt.eventType}" }
        }
    }

    private fun upsert(evt: ProfessionalEvent) {
        val current = repo.findById(evt.id).orElse(
            ProfessionalDocument(id = evt.id)
        )

        current.fullName = evt.fullName ?: current.fullName
        current.email = evt.email ?: current.email
        current.phone = evt.phone ?: current.phone
        current.skills = evt.skills ?: current.skills
        current.rating = evt.rating ?: current.rating
        current.location = evt.location ?: current.location
        current.dailyRate = evt.dailyRate ?: current.dailyRate
        current.employmentState = evt.employmentState ?: current.employmentState
        current.notes = evt.notes ?: current.notes

        current.lastEventType = evt.eventType
        current.lastChangedFields = evt.changedFields ?: emptySet()
        current.lastOccurredAt = evt.occurredAt

        repo.save(current)
        logger.info { "Professional upsert id=${evt.id} type=${evt.eventType}" }
    }

    private fun delete(id: Long) {
        repo.deleteById(id)
        logger.info { "Professional deleted id=$id" }
    }
}
