package it.polito.wa2.g19.communication_manager

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g19.communication_manager.dtos.SendEmailDTO
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CRMApplicationTests(@Autowired private val mockMvc: MockMvc, @Autowired val objectMapper: ObjectMapper) {
    private lateinit var testMail: SendEmailDTO
    private lateinit var testMailWrong: SendEmailDTO

    @BeforeAll
    fun setup() {
        testMail = SendEmailDTO(
            recipient = "daniele.dr00@gmail.com",
            subject = "Test Subject",
            body = "Test Body"
        )

        testMailWrong = SendEmailDTO(
            recipient = "invalid email",
            subject = "Test Subject",
            body = "Test Body"
        )
    }

    @Test
    fun contextLoads() {
    }

    @Test
    fun testSendEmail_status200() {
        mockMvc.post("/API/emails/") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testMail)
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }
    }

    @Test
    fun testSendEmail_status400() {
        mockMvc.post("/API/emails/") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testMailWrong)
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isBadRequest() } }
    }
}
