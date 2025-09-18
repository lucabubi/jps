package it.polito.wa2.g19.communication_manager.unit

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.verify
import it.polito.wa2.g19.communication_manager.dtos.SendEmailDTO
import it.polito.wa2.g19.communication_manager.exceptions.InvalidEmailException
import it.polito.wa2.g19.communication_manager.services.CMService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class CRM2ControllerTests(@Autowired val mockMvc: MockMvc, @Autowired val objectMapper: ObjectMapper) {
    @MockkBean
    lateinit var cmService: CMService

    @Test
    fun whenSendEmail_thenResponse200() {
        // Arrange
        val sendEmailDTO = SendEmailDTO(
            recipient = "daniele.dr00@gmail.com",
            subject = "subject",
            body = "body"
        )
        every { cmService.sendEmail(sendEmailDTO) } returns sendEmailDTO

        // Act & Assert
        mockMvc.perform(post("/API/emails/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sendEmailDTO)))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(sendEmailDTO)))

        verify { cmService.sendEmail(sendEmailDTO) }
    }

    @Test
    fun whenSendEmail_thenInvalidEmailException() {
        // Arrange
        val sendEmailDTO = SendEmailDTO(
            recipient = "invalid_email",
            subject = "subject",
            body = "body"
        )
        every { cmService.sendEmail(sendEmailDTO) } throws InvalidEmailException("Invalid email address")

        // Act & Assert
        mockMvc.perform(post("/API/emails/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sendEmailDTO)))
            .andExpect(status().isBadRequest)

    }

}