package it.polito.wa2.g19.crm.unit

import io.mockk.*
import it.polito.wa2.g19.crm.dtos.*
import it.polito.wa2.g19.crm.entities.*
import it.polito.wa2.g19.crm.exceptions.*
import it.polito.wa2.g19.crm.repositories.*
import it.polito.wa2.g19.crm.services.CustomerServiceImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*

class CustomerServiceTests {
    private val customerRepository: CustomerRepository = mockk()
    private val noteRepository: NoteRepository = mockk()
    private val customerService =
        CustomerServiceImpl(
            customerRepository,
            noteRepository
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
                telephones= emptySet(),
                region = Region.NA),
            notes = listOf(
                NoteDTO(id = 1L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                NoteDTO(id = 2L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
            ),
            jobOffers = emptySet()
        )
        val customerSlot = slot<Customer>()
        every { customerRepository.save(capture(customerSlot)) } answers { customerSlot.captured }
        every { noteRepository.save(any()) } answers { firstArg() }  // Add this line


        // Act
        val result = customerService.createCustomer(customerDTO)

        // Assert
        verify { customerRepository.save(any()) }
        assertEquals(customerDTO, result)
        assertEquals(customerDTO.contact, customerSlot.captured.contact.toDTO())
        assertEquals(customerDTO.notes, customerSlot.captured.notes.map { it.toDTO() })
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
                emails= mutableSetOf(),
                addresses= mutableSetOf(),
                telephones= mutableSetOf(),
                region = Region.NA),
            notes = mutableSetOf(
                Note(id = 3L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                Note(id = 4L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
            ),
            jobOffers = mutableSetOf()
        )
        val customer2 = Customer(
            contact = Contact(
                name= "Jane",
                surname= "Doe",
                category= Category.CUSTOMER,
                emails= mutableSetOf(),
                addresses= mutableSetOf(),
                telephones= mutableSetOf(),
                region = Region.NA),
            notes = mutableSetOf(
                Note(id = 5L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                Note(id = 6L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
            ),
            jobOffers = mutableSetOf()
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
                emails= mutableSetOf(),
                addresses= mutableSetOf(),
                telephones= mutableSetOf(),
                region = Region.NA),
            notes = mutableSetOf(
                Note(id = 7L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                Note(id = 8L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
            ),
            jobOffers = mutableSetOf()
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
        val updatedNotes = listOf(
            NoteDTO(id = 3L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
            NoteDTO(id = 4L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
        )
        val contactDTO = ContactDTO(
            id = 1L,
            name = "John",
            surname = "Doe",
            category = Category.CUSTOMER,
            emails = emptySet(),
            addresses = emptySet(),
            telephones = emptySet(),
            region = Region.NA
        )
        val customerDTO = CustomerDTO(
            id = customerId,
            contact = contactDTO,
            notes = listOf(
                NoteDTO(id = 9L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                NoteDTO(id = 10L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
            ),
            jobOffers = emptySet()
        )
        //given
        every { customerRepository.findById(customerId) } returns Optional.of(customerDTO.toEntity())
        every { customerRepository.save(any()) } answers { firstArg() }
        every { noteRepository.save(any()) } answers { firstArg() }  // Add this line

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
        val updatedNotes = listOf(NoteDTO(id = 9L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
            NoteDTO(id = 10L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now()),
            NoteDTO(id = 11L, title = "Note 3", description = "Description 3", createdAt = LocalDateTime.now()))
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