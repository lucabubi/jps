package it.polito.wa2.g19.analytics.listeners

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.polito.wa2.g19.analytics.events.JobOfferEvent
import it.polito.wa2.g19.analytics.models.JobOfferDocument
import it.polito.wa2.g19.analytics.repositories.JobOfferRepository
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.Instant

private val logger = KotlinLogging.logger {}

@Component
class JobOfferListener(
    private val repo: JobOfferRepository,
    private val om: ObjectMapper
) {
    @KafkaListener(
        topics = ["jobOfferTopic"]
    )
    fun onMessage(value: String) {
        val evt = om.readValue<JobOfferEvent>(value)
        logger.info { "JobOfferEvent ${evt.eventType} id=${evt.id}" }

        when (evt.eventType.uppercase()) {
            "DELETED" -> repo.deleteById(evt.id)
            else -> {
                val doc = repo.findById(evt.id).orElse(JobOfferDocument(id = evt.id))
                doc.description = evt.description
                doc.status = evt.status
                doc.duration = evt.duration
                doc.notes = evt.notes
                doc.requiredSkills = evt.requiredSkills
                doc.customerId = evt.customerId
                doc.professionalId = evt.professionalId
                doc.value = evt.value
                doc.lastEventType = evt.eventType
                doc.lastChangedFields = evt.changedFields
                doc.lastOccurredAt = evt.occurredAt ?: Instant.now().toEpochMilli()
                repo.save(doc)
            }
        }
    }
}
