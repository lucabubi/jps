package it.polito.wa2.g19.crm.unit

import io.mockk.*
import it.polito.wa2.g19.crm.dtos.*
import it.polito.wa2.g19.crm.entities.*
import it.polito.wa2.g19.crm.exceptions.*
import it.polito.wa2.g19.crm.repositories.*
import it.polito.wa2.g19.crm.services.CustomerServiceImpl
import it.polito.wa2.g19.crm.kafka.CustomerEventsProducer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import java.util.*

class CustomerServiceTests {
    private val customerRepository: CustomerRepository = mockk()
    private val customerEventsProducer: CustomerEventsProducer = mockk(relaxed = true)
    private val customerService =
        CustomerServiceImpl(
            customerRepository,
            customerEventsProducer
        )


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
        val customerSlot = slot<Customer>()
        every { customerRepository.save(capture(customerSlot)) } answers { customerSlot.captured }

        // Act
        val result = customerService.createCustomer(customerDTO)

        // Assert
        verify { customerRepository.save(any()) }
        assertEquals(customerDTO, result)
        assertEquals(customerDTO.contact, customerSlot.captured.contact.toDTO())
        assertEquals(customerDTO.notes, customerSlot.captured.notes)
        assertEquals(customerDTO.jobOffers, customerSlot.captured.jobOffers.map { it.toDTO() }.toSet())
    }


    @Test
    fun whenGetCustomers_thenReturnCustomerDTOList() {
        // Arrange
        val customer1 = Customer(
            contact = Contact(
                name= "John",
                surname= "Doe",
                category= Category.CUSTOMER,
                emails= emptySet(),
                addresses= emptySet(),
                telephones= emptySet(),),
            notes = listOf("Note 1", "Note 2"),
            jobOffers = emptySet()
        )
        val customer2 = Customer(
            contact = Contact(
                name= "Jane",
                surname= "Doe",
                category= Category.CUSTOMER,
                emails= emptySet(),
                addresses= emptySet(),
                telephones= emptySet(),),
            notes = listOf("Note 2", "Note 3"),
            jobOffers = emptySet()
        )
        val customers = listOf(customer1, customer2)
        every { customerRepository.findAll() } returns customers

        // Act
        val result = customerService.getCustomers()

        // Assert
        verify { customerRepository.findAll() }
        assertEquals(customers.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetCustomer_thenReturnCustomerDTO() {
        // Arrange
        val id = 1L
        val customer = Customer(
            id = id,
            contact = Contact(
                name= "John",
                surname= "Doe",
                category= Category.CUSTOMER,
                emails= emptySet(),
                addresses= emptySet(),
                telephones= emptySet(),),
            notes = listOf("Note 1", "Note 2"),
            jobOffers = emptySet()
        )
        every { customerRepository.findById(id) } returns Optional.of(customer)

        // Act
        val result = customerService.getCustomer(id)

        // Assert
        verify { customerRepository.findById(id) }
        assertEquals(customer.toDTO(), result)
    }

    @Test
    fun whenGetCustomer_thenThrowCustomerNotFoundException() {
        // Arrange
        val id = 1L
        every { customerRepository.findById(id) } returns Optional.empty()

        // Act & Assert
        assertThrows<CustomerNotFoundException> {
            customerService.getCustomer(id)
        }
    }

    @Test
    fun whenUpdateCustomerNotes_thenReturnCustomerDTO() {
        //mock data
        val customerId = 1L
        val updatedNotes = listOf("Note 1", "Note 2", "Note 3")
        val contactDTO = ContactDTO(
            id = 1L,
            name = "John",
            surname = "Doe",
            category = Category.CUSTOMER,
            emails = emptySet(),
            addresses = emptySet(),
            telephones = emptySet()
        )
        val customerDTO = CustomerDTO(
            id = customerId,
            contact = contactDTO,
            notes = listOf("Note 1", "Note 2"),
            jobOffers = emptySet()
        )
        //given
        every { customerRepository.findById(customerId) } returns Optional.of(customerDTO.toEntity())
        every { customerRepository.save(any()) } answers { firstArg() }
        //when
        val result = customerService.updateCustomerNotes(customerId, updatedNotes)
        //then
        verify { customerRepository.findById(customerId) }
        verify { customerRepository.save(any()) }
        assertEquals(updatedNotes, result.notes)
    }

    @Test
    fun whenUpdateCustomerNotes_thenThrowCustomerNotFoundException() {
        //mock data
        val customerId = 1L
        val updatedNotes = listOf("Note 1", "Note 2", "Note 3")
        //given
        every { customerRepository.findById(customerId) } returns Optional.empty()
        //when
        val exception = assertThrows<CustomerNotFoundException> {
            customerService.updateCustomerNotes(customerId, updatedNotes)
        }
        //then
        verify { customerRepository.findById(customerId) }
        assertEquals("Contact with id $customerId not found", exception.message)
    }



}