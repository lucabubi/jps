package it.polito.wa2.g19.analytics.listeners

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.type.TypeReference // <-- Aggiungi import
import it.polito.wa2.g19.analytics.events.DebeziumEvent // <-- Importa la nuova classe
import it.polito.wa2.g19.analytics.repositories.JobOfferRepository
import it.polito.wa2.g19.analytics.events.JobOfferPayload // <-- Importa il nuovo Payload
import it.polito.wa2.g19.analytics.models.JobOfferDocument
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.Instant


private val logger = KotlinLogging.logger {}

@Component
class JobOfferListener(
    private val repo: JobOfferRepository,
    private val om: ObjectMapper
) { @KafkaListener(
        topics = ["\${app.kafka.job-offer-topic}"]
    )
    fun onMessage(value: String) {

    val evt = om.readValue(
        value,
        object : TypeReference<DebeziumEvent<JobOfferPayload>>() {}
    )

    val payload = evt.payload ?: return

    logger.info { "Debezium JobOffer Event op=${payload.op}" }

    when (payload.op) {
        "d" -> { // DELETE
            val deletedData = payload.before ?: return // Nei delete, i dati sono in 'before'
            logger.info { "Deleting JobOffer id=${deletedData.id}" }
            repo.deleteById(deletedData.id)
        }

        "c", "u", "r" -> { // CREATE, UPDATE, o READ (dallo snapshot iniziale)
            val data = payload.after ?: return // I dati della riga sono in 'after'
            logger.info { "Upserting JobOffer id=${data.id}" }

            // La tua logica "upsert" è già perfetta
            val doc = repo.findById(data.id).orElse(JobOfferDocument(id = data.id))

            // Mappiamo i campi dal payload 'after'
            doc.description = data.description
            doc.status = data.status
            doc.duration = data.duration
            doc.notes = data.notes
            doc.requiredSkills = data.requiredSkills
            doc.customerId = data.customerId
            doc.professionalId = data.professionalId
            doc.value = data.value

            // Mappiamo i metadati dall'evento Debezium
            doc.lastEventType = payload.op
            doc.lastOccurredAt = payload.tsMs ?: Instant.now().toEpochMilli()

            // Nota: Calcolare i 'changedFields' richiede un confronto
            // tra payload.before e payload.after. Per ora lo lasciamo nullo.
            doc.lastChangedFields = null

            repo.save(doc)
        }

        else -> {
            logger.warn { "Operazione Debezium non gestita: ${payload.op}" }
        }
    }
}
}