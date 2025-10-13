package it.polito.wa2.g19.crm.kafka

import it.polito.wa2.g19.crm.events.CustomerEvent
import mu.KotlinLogging
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CustomerEventsProducer(
    private val template: KafkaTemplate<Long, CustomerEvent>,
    @param:Qualifier("customerTopic") private val customerTopic: NewTopic
) {
    private val topic = customerTopic.name()

    fun publish(evt: CustomerEvent) {
        template.send(topic, evt.id, evt).whenComplete { res, ex ->
            if (ex != null) {
                logger.error(ex) { "Publish KO topic=$topic key=${evt.id} type=${evt.eventType}" }
            } else {
                val md = res.recordMetadata
                logger.info { "Publish OK $topic [${md.partition()}@${md.offset()}] key=${evt.id} type=${evt.eventType} changed=${evt.changedFields}" }
            }
        }
    }
}
