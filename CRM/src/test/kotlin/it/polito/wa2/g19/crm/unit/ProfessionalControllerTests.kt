package it.polito.wa2.g19.crm.unit

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import it.polito.wa2.g19.crm.controllers.ProfessionalController
import it.polito.wa2.g19.crm.dtos.ContactDTO
import it.polito.wa2.g19.crm.dtos.NoteDTO
import it.polito.wa2.g19.crm.dtos.ProfessionalDTO
import it.polito.wa2.g19.crm.entities.Category
import it.polito.wa2.g19.crm.entities.Professional
import it.polito.wa2.g19.crm.entities.Region
import it.polito.wa2.g19.crm.exceptions.ProfessionalNotFoundException
import it.polito.wa2.g19.crm.services.ProfessionalService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@ActiveProfiles("h2")
@WebMvcTest(
    controllers = [ProfessionalController::class],
    excludeFilters = [ComponentScan.Filter(type = FilterType.ANNOTATION, classes = [EnableWebSecurity::class])]
)
@AutoConfigureMockMvc(addFilters = false)
class ProfessionalControllerTests(@param:Autowired val mockMvc: MockMvc, @param:Autowired val objectMapper: ObjectMapper) {
    @MockkBean
    lateinit var professionalService: ProfessionalService

    @Test
    fun whenCreateProfessional_thenReturnCreatedProfessionalDTO() {
        // Arrange
        val professionalDTO = ProfessionalDTO(
            contact = ContactDTO(
                name= "John",
                surname= "Doe",
                category= Category.PROFESSIONAL,
                emails= emptySet(),
                addresses= emptySet(),
                telephones= emptySet(),
                region = Region.NA,
                notes = setOf(
                    NoteDTO(id = 1L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 2L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                )
            ),
            skills = emptySet(),
            dailyRate = 100f,
            employmentState = Professional.State.AVAILABLE_FOR_WORK,
            location = "Turin"
        )
        every { professionalService.createProfessional(professionalDTO) } returns professionalDTO

        // Act & Assert
        mockMvc.perform(
            post("/API/professionals/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(professionalDTO)))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(professionalDTO)))

        verify { professionalService.createProfessional(professionalDTO) }
    }

    @Test
    fun whenGetProfessional_thenReturnProfessionalDTO() {
        // Arrange
        val id = 1L
        val professionalDTO = ProfessionalDTO(
            contact = ContactDTO(
                name= "John",
                surname= "Doe",
                category= Category.PROFESSIONAL,
                emails= emptySet(),
                addresses= emptySet(),
                telephones= emptySet(),
                region = Region.NA,
                notes = setOf(
                    NoteDTO(id = 3L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 4L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                )
            ),
            skills = emptySet(),
            dailyRate = 100f,
            employmentState = Professional.State.AVAILABLE_FOR_WORK,
            location = "Turin"
        )
        every { professionalService.getProfessional(id) } returns professionalDTO

        // Act & Assert
        mockMvc.perform(get("/API/professionals/$id"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(professionalDTO)))

        verify { professionalService.getProfessional(id) }
    }

    @Test
    fun whenGetProfessional_thenThrowProfessionalNotFoundException() {
        // Arrange
        val id = 1L
        every { professionalService.getProfessional(id) } throws ProfessionalNotFoundException("Professional with id: $id not found")

        // Act & Assert
        mockMvc.perform(get("/API/professionals/$id"))
            .andExpect(status().isNotFound)
    }
}