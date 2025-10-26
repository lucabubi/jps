package it.polito.wa2.g19.analytics.listeners

import com.fasterxml.jackson.core.type.TypeReference // <-- Importa TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g19.analytics.events.CustomerPayload // <-- Importa il nuovo Payload
import it.polito.wa2.g19.analytics.events.DebeziumEvent // <-- Importa l'evento generico
import it.polito.wa2.g19.analytics.models.CustomerDocument
import it.polito.wa2.g19.analytics.repositories.CustomerRepository
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.time.Instant

private val logger = KotlinLogging.logger {}

@Service
class CustomerListener(
    private val repo: CustomerRepository,
    private val om: ObjectMapper
) {

    // Niente più data class CustomerEvent qui!

    @KafkaListener(
        topics = ["\${app.kafka.customer-topic}"], // <-- 1. Topic aggiornato
        groupId = "analytics-crm" // Il group-id va bene
    )
    fun onMessage(value: String) {

        // 2. Deserializza usando il tipo generico DebeziumEvent<CustomerPayload>
        val evt = om.readValue<DebeziumEvent<CustomerPayload>>(
            value,
            object : TypeReference<DebeziumEvent<CustomerPayload>>() {}
        )

        val payload = evt.payload ?: return
        logger.info { "Debezium Customer Event op=${payload.op}" }

        // 3. Logica basata su 'op'
        when (payload.op) {
            "c", "u", "r" -> upsert(payload.after, payload.op, payload.tsMs)
            "d" -> delete(payload.before)
            else -> logger.warn { "Evento customer ignorato: op=${payload.op}" }
        }
    }

    private fun upsert(data: CustomerPayload?, op: String?, ts: Long?) {
        if (data == null) {
            logger.warn { "Ricevuto evento upsert (op=$op) con payload 'after' nullo." }
            return
        }

        // La tua logica di upsert era già buona, la adattiamo
        val current = repo.findById(data.id).orElse(
            CustomerDocument(id = data.id)
        )

        // Mappa da CustomerPayload (dati da 'after')
        current.fullName = data.fullName
        current.email = data.email
        current.phone = data.phone
        current.tags = data.tags
        current.notes = data.notes

        // Mappa metadati da Debezium
        current.lastEventType = op
        current.lastOccurredAt = ts ?: Instant.now().toEpochMilli()
        current.lastChangedFields = null // (Calcolo complesso, per ora nullo)

        repo.save(current)
        logger.info { "Customer upsert id=${data.id} op=$op" }
    }

    private fun delete(data: CustomerPayload?) {
        if (data == null) {
            logger.warn { "Ricevuto evento delete con payload 'before' nullo." }
            return
        }
        repo.deleteById(data.id)
        logger.info { "Customer deleted id=${data.id}" }
    }
}