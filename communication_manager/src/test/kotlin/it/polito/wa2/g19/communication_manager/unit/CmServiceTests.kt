package it.polito.wa2.g19.communication_manager.services

import it.polito.wa2.g19.communication_manager.EmailRoute
import it.polito.wa2.g19.communication_manager.dtos.SendEmailDTO
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import org.springframework.stereotype.Service
import mu.KotlinLogging

@Service
class CMServiceImpl(private val emailRoute: EmailRoute) : CMService {
    private var camelContext: CamelContext = DefaultCamelContext()
    private val logger = KotlinLogging.logger {}

    init {
        camelContext.addRoutes(emailRoute)
        camelContext.start()
    }

    fun setCamelContext(context: CamelContext) {
        this.camelContext = context
    }

    override fun sendEmail(sendEmailDTO: SendEmailDTO): SendEmailDTO {
        val producerTemplate = camelContext.createProducerTemplate()
        logger.info { "Sending email to ${sendEmailDTO.recipient}" }
        producerTemplate.sendBodyAndHeaders(
            "direct:sendEmail",
            sendEmailDTO.body,
            mapOf(
                "to" to sendEmailDTO.recipient,
                "subject" to sendEmailDTO.subject
            )
        )
        return sendEmailDTO
    }
}