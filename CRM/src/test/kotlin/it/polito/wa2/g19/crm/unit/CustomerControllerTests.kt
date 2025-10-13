package it.polito.wa2.g19.crm.unit
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.verify
import it.polito.wa2.g19.crm.dtos.*
import it.polito.wa2.g19.crm.entities.*
import it.polito.wa2.g19.crm.exceptions.*
import it.polito.wa2.g19.crm.services.CustomerService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class CustomerControllerTests(@param:Autowired val mockMvc: MockMvc, @param:Autowired val objectMapper: ObjectMapper) {

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
                telephones= emptySet(),),
            notes = listOf("Note 1", "Note 2"),
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
                telephones= emptySet(),),
            notes = listOf("Note 1", "Note 2"),
            jobOffers = emptySet()
        )
        val customerDTO2 = CustomerDTO(
            contact = ContactDTO(
                name= "Jane",
                surname= "Doe",
                category= Category.CUSTOMER,
                emails= emptySet(),
                addresses= emptySet(),
                telephones= emptySet(),),
            notes = listOf("Note 3", "Note 4"),
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
                telephones= emptySet(),),
            notes = listOf("Note 1", "Note 2"),
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