package it.polito.wa2.g19.analytics.listeners

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g19.analytics.events.DebeziumEvent
import it.polito.wa2.g19.analytics.events.ProfessionalPayload
import it.polito.wa2.g19.analytics.models.ProfessionalDocument
import it.polito.wa2.g19.analytics.repositories.ProfessionalRepository
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.time.Instant

private val logger = KotlinLogging.logger {}

@Service
class ProfessionalListener(
    private val repo: ProfessionalRepository,
    private val om: ObjectMapper
) {

    @KafkaListener(
        topics = ["\${app.kafka.professional-topic}"], // <-- 1. Topic aggiornato
        groupId = "analytics-crm"
    )
    fun onMessage(value: String) {

        val evt = om.readValue(
            value,
            object : TypeReference<DebeziumEvent<ProfessionalPayload>>() {}
        )

        val payload = evt.payload ?: return
        logger.info { "Debezium Professional Event op=${payload.op}" }

        // 3. Logica basata su 'op'
        when (payload.op) {
            "c", "u", "r" -> upsert(payload.after, payload.op, payload.tsMs)
            "d" -> delete(payload.before)
            else -> logger.warn { "Evento professional ignorato: op=${payload.op}" }
        }
    }

    private fun upsert(data: ProfessionalPayload?, op: String?, ts: Long?) {
        if (data == null) {
            logger.warn { "Ricevuto evento upsert (op=$op) con payload 'after' nullo." }
            return
        }

        val current = repo.findById(data.id).orElse(
            ProfessionalDocument(id = data.id)
        )

        current.fullName = data.fullName
        current.email = data.email
        current.phone = data.phone
        current.skills = data.skills
        current.rating = data.rating
        current.location = data.location
        current.dailyRate = data.dailyRate
        current.employmentState = data.employmentState
        current.notes = data.notes

        current.lastEventType = op
        current.lastOccurredAt = ts ?: Instant.now().toEpochMilli()
        current.lastChangedFields = null // (Calcolo complesso, per ora nullo)

        repo.save(current)
        logger.info { "Professional upsert id=${data.id} op=$op" }
    }

    private fun delete(data: ProfessionalPayload?) {
        if (data == null) {
            logger.warn { "Ricevuto evento delete con payload 'before' nullo." }
            return
        }
        repo.deleteById(data.id)
        logger.info { "Professional deleted id=${data.id}" }
    }
}