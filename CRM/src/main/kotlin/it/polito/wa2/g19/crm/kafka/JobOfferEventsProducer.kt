package it.polito.wa2.g19.crm.kafka

import it.polito.wa2.g19.crm.events.JobOfferEvent
import mu.KotlinLogging
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class JobOfferEventsProducer(
    private val template: KafkaTemplate<Long, JobOfferEvent>,
    @param:Qualifier("jobOfferTopic") private val jobOfferTopic: NewTopic
) {
    private val topic = jobOfferTopic.name()

    fun publish(evt: JobOfferEvent) {
        template.send(topic, evt.id, evt).whenComplete { res, ex ->
            if (ex != null) logger.error(ex) { "KO publish topic=$topic key=${evt.id} type=${evt.eventType}" }
            else logger.info { "OK publish $topic [${res.recordMetadata.partition()}@${res.recordMetadata.offset()}] key=${evt.id} type=${evt.eventType} changed=${evt.changedFields}" }
        }
    }
}
