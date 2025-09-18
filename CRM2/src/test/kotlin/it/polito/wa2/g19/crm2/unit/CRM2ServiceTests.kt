package it.polito.wa2.g19.crm2.unit

import io.mockk.*
import it.polito.wa2.g19.crm2.dtos.*
import it.polito.wa2.g19.crm2.entities.*
import it.polito.wa2.g19.crm2.exceptions.*
import it.polito.wa2.g19.crm2.repositories.*
import it.polito.wa2.g19.crm2.services.CRM2ServiceImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.*

class CRM2ServiceTests {
    private val customerRepository: CustomerRepository = mockk()
    private val jobOfferRepository: JobOfferRepository = mockk()
    private val professionalRepository: ProfessionalRepository = mockk()
    private val messageRepository: MessageRepository = mockk()
    private val contactRepository: ContactRepository = mockk()
    private val telephoneRepository: TelephoneRepository = mockk()
    private val emailRepository: EmailRepository = mockk()
    private val addressRepository: AddressRepository = mockk()
    private val crm2Service =
        CRM2ServiceImpl(
            customerRepository,
            jobOfferRepository,
            professionalRepository,
            messageRepository,
            contactRepository,
            telephoneRepository,
            emailRepository,
            addressRepository
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
        val result = crm2Service.createCustomer(customerDTO)

        // Assert
        verify { customerRepository.save(any()) }
        assertEquals(customerDTO, result)
        assertEquals(customerDTO.contact, customerSlot.captured.contact.toDTO())
        assertEquals(customerDTO.notes, customerSlot.captured.notes)
        assertEquals(customerDTO.jobOffers, customerSlot.captured.jobOffers.map { it.toDTO() }.toSet())
    }

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
                telephones= emptySet(),),
            notes = listOf("Note 1", "Note 2"),
            skills = emptySet(),
            dailyRate = 100f,
            employmentState = Professional.State.AVAILABLE_FOR_WORK,
            location = "Location"
        )
        val professionalSlot = slot<Professional>()
        every { professionalRepository.save(capture(professionalSlot)) } answers { professionalSlot.captured }

        // Act
        val result = crm2Service.createProfessional(professionalDTO)

        // Assert
        verify { professionalRepository.save(any()) }
        assertEquals(professionalDTO, result)
        assertEquals(professionalDTO.contact, professionalSlot.captured.contact.toDTO())
        assertEquals(professionalDTO.notes, professionalSlot.captured.notes)
        assertEquals(professionalDTO.skills, professionalSlot.captured.skills)
        assertEquals(professionalDTO.dailyRate, professionalSlot.captured.dailyRate)
        assertEquals(professionalDTO.employmentState, professionalSlot.captured.employmentState)
        assertEquals(professionalDTO.location, professionalSlot.captured.location)
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
        val result = crm2Service.getCustomers()

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
        val result = crm2Service.getCustomer(id)

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
            crm2Service.getCustomer(id)
        }
    }

    @Test
    fun whenGetProfessional_thenReturnProfessionalDTO() {
        // Arrange
        val id = 1L
        val professional = Professional(
            id = id,
            contact = Contact(
                name= "John",
                surname= "Doe",
                category= Category.PROFESSIONAL,
                emails= emptySet(),
                addresses= emptySet(),
                telephones= emptySet(),),
            notes = listOf("Note 1", "Note 2"),
            skills = emptySet(),
            dailyRate = 100f,
            employmentState = Professional.State.AVAILABLE_FOR_WORK,
            location = "Turin"
        )
        every { professionalRepository.findById(id) } returns Optional.of(professional)

        // Act
        val result = crm2Service.getProfessional(id)

        // Assert
        verify { professionalRepository.findById(id) }
        assertEquals(professional.toDTO(), result)
    }

    @Test
    fun whenGetProfessional_thenThrowProfessionalNotFoundException() {
        // Arrange
        val id = 1L
        every { professionalRepository.findById(id) } returns Optional.empty()

        // Act & Assert
        assertThrows<ProfessionalNotFoundException> {
            crm2Service.getProfessional(id)
        }
    }

    @Test
    fun whenGetJobOffers_thenReturnJobOffersDTOList(){
        val pageable = PageRequest.of(0, 5)
        val jobOffersList = listOf(
            JobOffer(
                1L,
                "First job offer",
                JobOffer.Status.CREATED,
                2,
                listOf("good", "fine"),
                setOf("smart", "group work"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe")
                )
            ),
            JobOffer(
                2L,
                "First job offer",
                JobOffer.Status.CREATED,
                2,
                listOf("nice", "call again"),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe")
                )
            )
        )
        every { jobOfferRepository.findAll(pageable) } returns PageImpl(jobOffersList)

        val result = crm2Service.getJobOffers(pageable, null, null, null)

        verify{ jobOfferRepository.findAll(pageable) }
        assertEquals(jobOffersList.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetJobOffers_thenReturnJobOffersDTOListFiltered(){
        val pageable = PageRequest.of(0, 5)
        val professionalId = 1L
        val jobOffersList = listOf(
            JobOffer(
                1L,
                "First job offer",
                JobOffer.Status.CREATED,
                2,
                listOf("good", "fine"),
                setOf("smart", "group work"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe")
                )
            ),
            JobOffer(
                2L,
                "First job offer",
                JobOffer.Status.CREATED,
                2,
                listOf("nice", "call again"),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe")
                )
            )
        )
        every { jobOfferRepository.findAll(pageable) } returns PageImpl(jobOffersList.filter { it.professional?.id == professionalId })

        val result = crm2Service.getJobOffers(pageable, null, null, professionalId)

        verify{ jobOfferRepository.findAll(pageable) }
        assertEquals(jobOffersList.filter { it.professional?.id == professionalId }.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetJobOffers_thenReturnJobOffersDTOListTwoFiltered(){
        val pageable = PageRequest.of(0, 5)
        val customerId= 1L
        val status = JobOffer.Status.CREATED
        val jobOffersList = listOf(
            JobOffer(
                1L,
                "First job offer",
                JobOffer.Status.CREATED,
                2,
                listOf("good", "fine"),
                setOf("smart", "group work"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe")
                )
            ),
            JobOffer(
                2L,
                "First job offer",
                JobOffer.Status.SELECTION_PHASE,
                2,
                listOf("nice", "call again"),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe")
                )
            )
        )
        every { jobOfferRepository.findAll(pageable) } returns PageImpl(jobOffersList.filter { it.customer.id == customerId && it.status == status })

        val result = crm2Service.getJobOffers(pageable, customerId, status, null)

        verify{ jobOfferRepository.findAll(pageable) }
        assertEquals(jobOffersList.filter { it.customer.id == customerId && it.status == status}.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetOpenJobOffers_thenReturnJobOfferDTOList(){
        val pageable = PageRequest.of(0, 5)
        val jobOffersSet = setOf(
            JobOffer(
                1L,
                "First job offer",
                JobOffer.Status.CREATED,
                2,
                listOf("good", "fine"),
                setOf("smart", "group work"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe")
                )
            ),
            JobOffer(
                2L,
                "First job offer",
                JobOffer.Status.CREATED,
                2,
                listOf("nice", "call again"),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe")
                )
            )
        )
        val customer = Customer(
            id = 1L,
            contact = Contact(id = 1L, name = "John", surname = "Doe"),
            jobOffers = jobOffersSet,
            notes = emptyList()
        )



        every { customerRepository.findById(1L) } returns Optional.of(customer)

        val result = crm2Service.getOpenJobOffers(1L, pageable)

        verify{ customerRepository.findById(1L) }
        assertEquals(jobOffersSet.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetOpenJobOffers_thenReturnCustomerNotFound(){
        val pageable = PageRequest.of(0, 5)
        every { customerRepository.findById(1L) } returns Optional.empty()

        val exception = assertThrows<CustomerNotFoundException> {
            crm2Service.getOpenJobOffers(1L, pageable)
        }
        assertEquals("Customer with id 1 not found", exception.message)
        verify{ customerRepository.findById(1L) }
    }

    @Test
    fun whenGetAcceptedJobOffers_thenReturnJobOfferDTOList(){
        val pageable = PageRequest.of(0, 5)
        val jobOffersSet = setOf(
            JobOffer(
                1L,
                "First job offer",
                JobOffer.Status.DONE,
                2,
                listOf("good", "fine"),
                setOf("smart", "group work"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe")
                ),
                Professional(
                    id = 1L,
                    contact = Contact(id = 1L, name = "Jane", surname = "Austen"),
                    dailyRate = 100.0f
                )
            ),
            JobOffer(
                2L,
                "First job offer",
                JobOffer.Status.CONSOLIDATED,
                2,
                listOf("nice", "call again"),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe")
                ),
                Professional(
                    id = 2L,
                    contact = Contact(id = 2L, name = "Jane", surname = "Austen"),
                    dailyRate = 100.0f
                )
            )
        )

        val professional = Professional(
            id = 1L,
            contact = Contact(id = 1L, name = "Jane", surname = "Austen"),
            notes = emptyList(),
            jobOffers = jobOffersSet,
            skills = emptySet(),
        )

        every { professionalRepository.findById(1L) } returns Optional.of(professional)

        val result = crm2Service.getAcceptedJobOffers(1L, pageable)

        verify{ professionalRepository.findById(1L) }
        assertEquals(jobOffersSet.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetAcceptedJobOffers_thenReturnProfessionalNotFound(){
        val pageable = PageRequest.of(0, 5)
        every { professionalRepository.findById(1L) } returns Optional.empty()

        val exception = assertThrows<ProfessionalNotFoundException> {
            crm2Service.getAcceptedJobOffers(1L, pageable)
        }
        assertEquals("Professional with id 1 not found", exception.message)
        verify{ professionalRepository.findById(1L) }
    }

    @Test
    fun whenGetAbortedJobOffers_thenReturnJobOfferDTOList(){
        val pageable = PageRequest.of(0, 5)
        val jobOffersSet = setOf(
            JobOffer(
                1L,
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("good", "fine"),
                setOf("smart", "group work"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe")
                ),
                Professional(
                    id = 1L,
                    contact = Contact(id = 1L, name = "Jane", surname = "Austen"),
                    dailyRate = 100.0f
                )
            ),
            JobOffer(
                2L,
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("nice", "call again"),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe")
                ),
                Professional(
                    id = 2L,
                    contact = Contact(id = 2L, name = "Jane", surname = "Austen"),
                    dailyRate = 100.0f
                )
            )
        )


        every { jobOfferRepository.findAll() } returns jobOffersSet.toList()

        val result = crm2Service.getAbortedJobOffers(pageable, null, null)

        verify{ jobOfferRepository.findAll() }
        assertEquals(jobOffersSet.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetAbortedJobOffers_thenReturnFilteredByCustomerId(){
        val pageable = PageRequest.of(0, 5)
        val customer = Customer(
            id = 1L,
            contact = Contact(id = 1L, name = "John", surname = "Doe"),
            notes = emptyList(),
            jobOffers = emptySet()
        )
        val jobOffersList = listOf(
            JobOffer(
                1L,
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("good", "fine"),
                setOf("smart", "group work"),
                customer,
                Professional(
                    id = 1L,
                    contact = Contact(id = 1L, name = "Jane", surname = "Austen"),
                    dailyRate = 100.0f
                )
            ),
            JobOffer(
                2L,
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("nice", "call again"),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 2L,
                    contact = Contact(id = 2L, name = "John", surname = "Doe")
                ),
                Professional(
                    id = 2L,
                    contact = Contact(id = 2L, name = "Jane", surname = "Austen"),
                    dailyRate = 100.0f
                )
            )
        )

        customer.jobOffers.plus(jobOffersList[0])

        every { jobOfferRepository.findAll() } returns jobOffersList
        every { customerRepository.findById(1L) } returns Optional.of(customer)

        val result = crm2Service.getAbortedJobOffers(pageable, 1L, null)

        verify{ jobOfferRepository.findAll() }
        verify { customerRepository.findById(1L) }
        assertEquals(jobOffersList.filter { it.customer.id == 1L }.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetAbortedJobOffers_thenReturnFilteredByProfessionalId(){
        val pageable = PageRequest.of(0, 5)
        val professional = Professional(
            id = 1L,
            contact = Contact(id = 1L, name = "John", surname = "Doe"),
            notes = emptyList(),
            jobOffers = emptySet()
        )
        val jobOffersList = listOf(
            JobOffer(
                1L,
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("good", "fine"),
                setOf("smart", "group work"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe")
                ),
                professional
            ),
            JobOffer(
                2L,
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("nice", "call again"),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 2L,
                    contact = Contact(id = 2L, name = "John", surname = "Doe")
                ),
                Professional(
                    id = 2L,
                    contact = Contact(id = 2L, name = "Jane", surname = "Austen"),
                    dailyRate = 100.0f
                )
            )
        )

        professional.jobOffers.plus(jobOffersList[0])

        every { jobOfferRepository.findAll() } returns jobOffersList
        every { professionalRepository.findById(1L) } returns Optional.of(professional)

        val result = crm2Service.getAbortedJobOffers(pageable, null, 1L)

        verify{ jobOfferRepository.findAll() }
        verify { professionalRepository.findById(1L) }
        assertEquals(jobOffersList.filter { it.professional?.id == 1L }.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetAbortedJobOffers_thenReturnFilteredByCustomerIdAndProfessionalId(){
        val pageable = PageRequest.of(0, 5)

        val customer = Customer(
            id = 1L,
            contact = Contact(id = 1L, name = "John", surname = "Doe"),
            notes = emptyList(),
            jobOffers = emptySet()
        )

        val professional = Professional(
            id = 1L,
            contact = Contact(id = 1L, name = "John", surname = "Doe"),
            notes = emptyList(),
            jobOffers = emptySet()
        )

        val jobOffersList = listOf(
            JobOffer(
                1L,
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("good", "fine"),
                setOf("smart", "group work"),
                customer,
                professional
            ),
            JobOffer(
                2L,
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("nice", "call again"),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 2L,
                    contact = Contact(id = 2L, name = "John", surname = "Doe")
                ),
                Professional(
                    id = 2L,
                    contact = Contact(id = 2L, name = "Jane", surname = "Austen"),
                    dailyRate = 100.0f
                )
            )
        )

        customer.jobOffers.plus(jobOffersList[0])
        professional.jobOffers.plus(jobOffersList[0])

        every { jobOfferRepository.findAll() } returns jobOffersList
        every { customerRepository.findById(1L) } returns Optional.of(customer)
        every { professionalRepository.findById(1L) } returns Optional.of(professional)

        val result = crm2Service.getAbortedJobOffers(pageable, 1L, 1L)

        verify{ jobOfferRepository.findAll() }
        verify { customerRepository.findById(1L) }
        verify { professionalRepository.findById(1L) }
        assertEquals(jobOffersList.filter { it.professional?.id == 1L && it.customer.id == 1L }.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetAbortedJobOffers_thenReturnCustomerNotFoundException(){
        val pageable = PageRequest.of(0, 5)
        every { customerRepository.findById(1L) } returns Optional.empty()
        every { jobOfferRepository.findAll() } returns emptyList()

        val exception = assertThrows<CustomerNotFoundException> {
            crm2Service.getAbortedJobOffers(pageable, 1L, null)
        }
        assertEquals("Customer with id 1 not found", exception.message)
        verify{ customerRepository.findById(1L) }
        verify { jobOfferRepository.findAll() }
    }

    @Test
    fun whenGetAbortedJobOffers_thenReturnProfessionalNotFoundException(){
        val pageable = PageRequest.of(0, 5)
        every { professionalRepository.findById(1L) } returns Optional.empty()
        every { jobOfferRepository.findAll() } returns emptyList()

        val exception = assertThrows<ProfessionalNotFoundException> {
            crm2Service.getAbortedJobOffers(pageable, null, 1L)
        }
        assertEquals("Professional with id 1 not found", exception.message)
        verify{ professionalRepository.findById(1L) }
        verify { jobOfferRepository.findAll() }
    }

    @Test
    fun updateJobOffer_thenReturnJobOfferDTO() {
        val jobOfferId = 1L
        val jobOfferUpdateDTO = JobOfferUpdateDTO(
            status = "SELECTION_PHASE",
            notes = Optional.of(listOf("good job"))
        )
        val jobOffer = JobOffer(
            1L,
            "First job offer",
            JobOffer.Status.CREATED,
            2,
            listOf("good", "fine"),
            setOf("smart", "group work"),
            Customer(
                id = 1L,
                contact = Contact(id = 1L, name = "John", surname = "Doe")
            )
        )
        val newJobOffer = JobOffer(
            1L,
            "First job offer",
            JobOffer.Status.SELECTION_PHASE,
            2,
            listOf("good job"),
            setOf("smart", "group work"),
            Customer(
                id = 1L,
                contact = Contact(id = 1L, name = "John", surname = "Doe")
            )
        )
        every { jobOfferRepository.findById(jobOfferId) } returns Optional.of(jobOffer)
        val result = crm2Service.updateJobOffer(jobOfferId, jobOfferUpdateDTO)
        verify { jobOfferRepository.findById(jobOfferId) }
        assertEquals(newJobOffer.toDTO(), result)
    }

    @Test
    fun updateJobOffer_thenReturnJobOfferNotFound() {
        val jobOfferId = 100L
        val jobOfferUpdateDTO = JobOfferUpdateDTO(
            status = "SELECTION_PHASE",
            notes = Optional.of(listOf("good job")),
            professionalId = Optional.of(1L)
        )
        every { jobOfferRepository.findById(jobOfferId) } returns Optional.empty()

        val exception = assertThrows<JobOfferNotFoundException> {
            crm2Service.updateJobOffer(jobOfferId, jobOfferUpdateDTO)
        }
        verify { jobOfferRepository.findById(jobOfferId)}
        assertEquals("Job offer with id $jobOfferId not found", exception.message)
    }

    @Test
    fun updateJobOffer_thenReturnInvalidStatus() {
        val jobOfferId = 1L
        val jobOfferUpdateDTO = JobOfferUpdateDTO(
            status = "INVALID_STATUS",
            notes = Optional.of(listOf("good job")),
            professionalId = Optional.of(1L)
        )
        val jobOffer = JobOffer(
            1L,
            "First job offer",
            JobOffer.Status.CREATED,
            2,
            listOf("good", "fine"),
            setOf("smart", "group work"),
            Customer(
                id = 1L,
                contact = Contact(id = 1L, name = "John", surname = "Doe")
            )
        )
        every { jobOfferRepository.findById(jobOfferId) } returns Optional.of(jobOffer)

        val exception = assertThrows<InvalidStatusException> {
            crm2Service.updateJobOffer(jobOfferId, jobOfferUpdateDTO)
        }
        assertEquals("Invalid status", exception.message)
        verify { jobOfferRepository.findById(jobOfferId)}

    }

    @Test
    fun getJobOfferValue_thenReturn(){
        val jobOfferId = 1L
        val jobOffer = JobOffer(
            1L,
            "First job offer",
            JobOffer.Status.SELECTION_PHASE,
            2,
            listOf("good", "fine", "good job"),
            setOf("smart", "group work"),
            Customer(
                id = 1L,
                contact = Contact(id = 1L, name = "John", surname = "Doe")
            ),
            Professional(
                id = 1L,
                contact = Contact(id = 1L, name = "Jane", surname = "Austen"),
                dailyRate = 100.0f
            ),
            value = 40.0f
        )
        every { jobOfferRepository.findById(jobOfferId) } returns Optional.of(jobOffer)

        val result = crm2Service.getJobOfferValue(jobOfferId)

        verify { jobOfferRepository.findById(jobOfferId) }
        assertEquals(40.0f, result)
    }

    @Test
    fun getJobOfferValue_thenReturnProfessionalNotAvailable(){
        val jobOfferId = 1L
        val jobOffer = JobOffer(
            1L,
            "First job offer",
            JobOffer.Status.SELECTION_PHASE,
            2,
            listOf("good", "fine", "good job"),
            setOf("smart", "group work"),
            Customer(
                id = 1L,
                contact = Contact(id = 1L, name = "John", surname = "Doe")
            )
        )
        every { jobOfferRepository.findById(jobOfferId) } returns Optional.of(jobOffer)

        val exception = assertThrows<ProfessionalNotAvailableException> {
            crm2Service.getJobOfferValue(jobOfferId)
        }
        assertEquals("No professional assigned", exception.message)
        verify { jobOfferRepository.findById(jobOfferId) }
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
        val result = crm2Service.updateCustomerNotes(customerId, updatedNotes)
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
            crm2Service.updateCustomerNotes(customerId, updatedNotes)
        }
        //then
        verify { customerRepository.findById(customerId) }
        assertEquals("Contact with id $customerId not found", exception.message)
    }

    @Test
    fun whenUpdateProfessional_thenReturnProfessionalDTO() {
        //mock data
        val professionalId = 1L
        val professionalUpdateDTO = ProfessionalUpdateDTO(
            dailyRate = Optional.of(200f),
            employmentState = Optional.of(Professional.State.AVAILABLE_FOR_WORK),
            location = Optional.of("Turin")
        )
        val contactDTO = ContactDTO(
            id = 1L,
            name = "John",
            surname = "Doe",
            category = Category.PROFESSIONAL,
            emails = emptySet(),
            addresses = emptySet(),
            telephones = emptySet()
        )
        val professionalDTO = ProfessionalDTO(
            id = professionalId,
            contact = contactDTO,
            notes = listOf("Note 1", "Note 2"),
            skills = emptySet(),
            dailyRate = 100f,
            employmentState = Professional.State.AVAILABLE_FOR_WORK,
            location = "Milan"
        )
        //given
        every { professionalRepository.findById(professionalId) } returns Optional.of(professionalDTO.toEntity())
        every { professionalRepository.save(any()) } answers { firstArg() }
        //when
        val result = crm2Service.updateProfessional(professionalId, professionalUpdateDTO)
        //then
        verify { professionalRepository.findById(professionalId) }
        verify { professionalRepository.save(any()) }
        assertEquals(professionalUpdateDTO.dailyRate.get(), result.dailyRate)
        assertEquals(professionalUpdateDTO.employmentState.get(), result.employmentState)
        assertEquals(professionalUpdateDTO.location.get(), result.location)
    }

    @Test
    fun whenUpdateProfessional_thenProfessionalNotAvailableException() {
        val professionalId = 1L
        val professionalUpdateDTO = ProfessionalUpdateDTO(
            dailyRate = Optional.of(200f),
            employmentState = Optional.of(Professional.State.AVAILABLE_FOR_WORK),
            location = Optional.of("Turin")
        )
        //given
        every { professionalRepository.findById(professionalId) } returns Optional.of(
            Professional(
                id = professionalId,
                contact = Contact(
                    name = "John",
                    surname = "Doe",
                    category = Category.PROFESSIONAL,
                    emails = emptySet(),
                    addresses = emptySet(),
                    telephones = emptySet()
                ),
                notes = listOf("Note 1", "Note 2"),
                skills = emptySet(),
                dailyRate = 100f,
                employmentState = Professional.State.EMPLOYED,
                location = "Milan",
                jobOffers = setOf(
                    JobOffer(
                        1L,
                        "First job offer",
                        JobOffer.Status.CONSOLIDATED,
                        2,
                        listOf("good", "fine", "good job"),
                        setOf("smart", "group work"),
                        Customer(
                            id = 1L,
                            contact = Contact(id = 1L, name = "John", surname = "Doe")
                        ),
                        Professional(
                            id = 1L,
                            contact = Contact(id = 1L, name = "Jane", surname = "Austen"),
                            dailyRate = 100.0f
                        ),
                        value = 40.0f
                    )
                )
            )
        )
        //when
        val exception = assertThrows<ProfessionalNotAvailableException> {
            crm2Service.updateProfessional(professionalId, professionalUpdateDTO)
        }
        //then
        verify { professionalRepository.findById(professionalId) }
        assertEquals("Professional with id $professionalId is currently working", exception.message)
    }

    @Test
    fun whenUpdateProfessional_thenThrowProfessionalNotFoundException() {
        //mock data
        val professionalId = 1L
        val professionalUpdateDTO = ProfessionalUpdateDTO(
            dailyRate = Optional.of(200f),
            employmentState = Optional.of(Professional.State.AVAILABLE_FOR_WORK),
            location = Optional.of("Turin")
        )
        //given
        every { professionalRepository.findById(professionalId) } returns Optional.empty()
        //when
        val exception = assertThrows<ProfessionalNotFoundException> {
            crm2Service.updateProfessional(professionalId, professionalUpdateDTO)
        }
        //then
        verify { professionalRepository.findById(professionalId) }
        assertEquals("Professional with id $professionalId not found", exception.message)
    }

    @Test
    fun whenGetProfessionals_thenReturnProfessionalDTOList() {
        //mock data
        val contactDTO1 = ContactDTO(
            id = 1L,
            name = "John",
            surname = "Doe",
            category = Category.PROFESSIONAL,
            emails = emptySet(),
            addresses = emptySet(),
            telephones = emptySet()
        )
        val contactDTO2 = ContactDTO(
            id = 2L,
            name = "Luca",
            surname = "Barbato",
            category = Category.PROFESSIONAL,
            emails = emptySet(),
            addresses = emptySet(),
            telephones = emptySet()
        )
        val professionalDTO1 = ProfessionalDTO(
            id = 1L,
            contact = contactDTO1,
            notes = listOf("Note 1", "Note 2"),
        )
        val professionalDTO2 = ProfessionalDTO(
            id = 2L,
            contact = contactDTO2,
            notes = listOf("Note 1", "Note 2"),
        )
        val professionals = listOf(professionalDTO1.toEntity(), professionalDTO2.toEntity())
        val pageable = PageRequest.of(0, 5)
        //given
        every { professionalRepository.findAll(pageable) } returns PageImpl(professionals)
        //when
        val result = crm2Service.getProfessionals(pageable, Optional.empty(), Optional.empty(), Optional.empty())
        //then
        verify { professionalRepository.findAll(pageable) }
        assertEquals(professionals.map { it.toDTO() }, result)
    }
}