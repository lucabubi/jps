package it.polito.wa2.g19.analytics.listeners

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.polito.wa2.g19.analytics.models.CustomerDocument
import it.polito.wa2.g19.analytics.repositories.CustomerRepository
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CustomerListener(
    private val repo: CustomerRepository,
    private val om: ObjectMapper
) {

    // Evento atteso in JSON (stesso contratto del CRM)
    data class CustomerEvent(
        val id: Long,
        val fullName: String? = null,
        val email: String? = null,
        val phone: String? = null,
        val tags: Set<String>? = null,
        val notes: List<String>? = null,
        val eventType: String,
        val changedFields: Set<String>? = null,
        val occurredAt: Long? = null
    )

    @KafkaListener(
        topics = ["customerTopic"],
        groupId = "analytics-crm"
    )
    fun onMessage(value: String) {
        val evt = om.readValue<CustomerEvent>(value)
        when (evt.eventType.uppercase()) {
            "CREATED", "UPDATED", "UPSERT" -> upsert(evt)
            "DELETED" -> delete(evt.id)
            else -> logger.warn { "Evento customer ignorato: type=${evt.eventType}" }
        }
    }

    private fun upsert(evt: CustomerEvent) {
        val current = repo.findById(evt.id).orElse(
            CustomerDocument(id = evt.id)
        )

        current.fullName = evt.fullName ?: current.fullName
        current.email = evt.email ?: current.email
        current.phone = evt.phone ?: current.phone
        current.tags = evt.tags ?: current.tags
        current.notes = evt.notes ?: current.notes

        current.lastEventType = evt.eventType
        current.lastChangedFields = evt.changedFields ?: emptySet()
        current.lastOccurredAt = evt.occurredAt

        repo.save(current)
        logger.info { "Customer upsert id=${evt.id} type=${evt.eventType}" }
    }

    private fun delete(id: Long) {
        repo.deleteById(id)
        logger.info { "Customer deleted id=$id" }
    }
}
