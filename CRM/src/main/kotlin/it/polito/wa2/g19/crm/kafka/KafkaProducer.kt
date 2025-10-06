package it.polito.wa2.g19.crm.kafka

import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service


@Service
class KafkaProducer(private val kafkaTemplate: KafkaTemplate<String, String>) {
    private val logger = KotlinLogging.logger {}
    fun sendMessage(topic: String, message: String) {
        kafkaTemplate.send(topic, message)
        logger.info { "Message sent: $message" }
    }

}