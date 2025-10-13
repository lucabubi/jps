package it.polito.wa2.g19.crm.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopicConfig {

    @Bean
    fun jobOfferTopic() : NewTopic {
        return TopicBuilder.name("jobOfferTopic").build()
    }

    @Bean
    fun customerTopic() : NewTopic {
        return TopicBuilder.name("customerTopic").build()
    }

    @Bean
    fun professionalTopic() : NewTopic {
        return TopicBuilder.name("professionalTopic").build()
    }

}