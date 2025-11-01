package it.polito.wa2.g19.crm.unit
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.verify
import it.polito.wa2.g19.crm.controllers.CustomerController
import it.polito.wa2.g19.crm.dtos.*
import it.polito.wa2.g19.crm.entities.*
import it.polito.wa2.g19.crm.exceptions.*
import it.polito.wa2.g19.crm.services.CustomerService
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@ActiveProfiles("h2")
@WebMvcTest(
    controllers = [CustomerController::class],
    excludeFilters = [ComponentScan.Filter(type = FilterType.ANNOTATION, classes = [EnableWebSecurity::class])]
)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTests(
    @param:Autowired val mockMvc: MockMvc,
    @param:Autowired val objectMapper: ObjectMapper
) {

    @MockkBean
    lateinit var customerService: CustomerService

    @Test
    fun whenCreateCustomer_thenReturnCreatedCustomerDTO() {
        // Arrange
        val customerDTO = CustomerDTO(
            contact = ContactDTO(
                name= "John",
                surname= "Doe",
                category= Category.CUSTOMER,
                emails= emptySet(),
                addresses= emptySet(),
                telephones= emptySet(),
                region = Region.NA,
                notes = setOf(
                    NoteDTO(id = 1L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 2L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                )),
            jobOffers = emptySet()
        )
        every { customerService.createCustomer(customerDTO) } returns customerDTO

        // Act & Assert
        mockMvc.perform(post("/API/customers/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(customerDTO)))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(customerDTO)))

        verify { customerService.createCustomer(customerDTO) }
    }

    @Test
    fun whenGetCustomers_thenReturnCustomerDTOList() {
        // Arrange
        val customerDTO1 = CustomerDTO(
            contact = ContactDTO(
                name= "John",
                surname= "Doe",
                category= Category.CUSTOMER,
                emails= emptySet(),
                addresses= emptySet(),
                telephones= emptySet(),
                region = Region.NA,
                notes = setOf(
                    NoteDTO(id = 1L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 2L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                )),
            jobOffers = emptySet()
        )
        val customerDTO2 = CustomerDTO(
            contact = ContactDTO(
                name= "Jane",
                surname= "Doe",
                category= Category.CUSTOMER,
                emails= emptySet(),
                addresses= emptySet(),
                telephones= emptySet(),
                region = Region.NA,
                notes = setOf(
                    NoteDTO(id = 3L, title = "Note 3", description = "Description 3", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 4L, title = "Note 4", description = "Description 4", createdAt = LocalDateTime.now())
                )),
            jobOffers = emptySet()
        )
        val customers = listOf(customerDTO1, customerDTO2)
        every { customerService.getCustomers() } returns customers

        // Act & Assert
        mockMvc.perform(get("/API/customers/"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(customers)))

        verify { customerService.getCustomers() }
    }

    @Test
    fun whenGetCustomer_thenReturnCustomerDTO() {
        // Arrange
        val id = 1L
        val customerDTO = CustomerDTO(
            contact = ContactDTO(
                name= "John",
                surname= "Doe",
                category= Category.CUSTOMER,
                emails= emptySet(),
                addresses= emptySet(),
                telephones= emptySet(),
                region = Region.NA,
                notes = setOf(
                    NoteDTO(id = 1L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 2L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                )),
            jobOffers = emptySet()
        )
        every { customerService.getCustomer(id) } returns customerDTO

        // Act & Assert
        mockMvc.perform(get("/API/customers/$id"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(customerDTO)))

        verify { customerService.getCustomer(id) }
    }

    @Test
    fun whenGetCustomer_thenThrowCustomerNotFoundException() {
        // Arrange
        val id = 1L
        every { customerService.getCustomer(id) } throws CustomerNotFoundException("Customer with id: $id not found")

        // Act & Assert
        mockMvc.perform(get("/API/customers/$id"))
            .andExpect(status().isNotFound)
    }


}