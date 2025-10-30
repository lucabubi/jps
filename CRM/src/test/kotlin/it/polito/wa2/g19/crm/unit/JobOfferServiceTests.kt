package it.polito.wa2.g19.crm.unit

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.polito.wa2.g19.crm.dtos.JobOfferUpdateDTO
import it.polito.wa2.g19.crm.dtos.NoteDTO
import it.polito.wa2.g19.crm.entities.Contact
import it.polito.wa2.g19.crm.entities.Customer
import it.polito.wa2.g19.crm.entities.JobOffer
import it.polito.wa2.g19.crm.entities.Professional
import it.polito.wa2.g19.crm.entities.Note
import it.polito.wa2.g19.crm.entities.Region
import it.polito.wa2.g19.crm.exceptions.*
import it.polito.wa2.g19.crm.services.JobOfferServiceImpl
import it.polito.wa2.g19.crm.repositories.CustomerRepository
import it.polito.wa2.g19.crm.repositories.JobOfferRepository
import it.polito.wa2.g19.crm.repositories.NoteRepository
import it.polito.wa2.g19.crm.repositories.ProfessionalRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import java.util.*

class JobOfferServiceTests {
    private val jobOfferRepository: JobOfferRepository = mockk()
    private val customerRepository: CustomerRepository = mockk()
    private val professionalRepository: ProfessionalRepository = mockk()
    private val noteRepository: NoteRepository = mockk()
    private val jobOfferService =
        JobOfferServiceImpl(
            jobOfferRepository,
            customerRepository,
            professionalRepository,
            noteRepository
        )

    @Test
    fun whenGetJobOffers_thenReturnJobOffersDTOList(){
        val pageable = PageRequest.of(0, 5)
        val jobOffersList = listOf(
            JobOffer(
                1L,
                "Important job offer",
                "First job offer",
                JobOffer.Status.CREATED,
                2,
                mutableSetOf(
                    Note(id = 1L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 2L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.EMEA)
                )
            ),
            JobOffer(
                2L,
                "Another job offer",
                "First job offer",
                JobOffer.Status.CREATED,
                2,
                mutableSetOf(
                    Note(id = 3L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 4L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                mutableSetOf("smart", "group work", "organised"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.EMEA)
                )
            )
        )
        every { jobOfferRepository.findAll(pageable) } returns PageImpl(jobOffersList)

        val result = jobOfferService.getJobOffers(pageable, null, null, null)

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
                "First job offer",
                JobOffer.Status.CREATED,
                2,
                mutableSetOf(
                    Note(id = 5L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 6L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.EMEA)
                )
            ),
            JobOffer(
                2L,
                "Second job offer",
                "First job offer",
                JobOffer.Status.CREATED,
                2,
                mutableSetOf(
                    Note(id = 7L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 8L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.EMEA)
                )
            )
        )
        every { jobOfferRepository.findAll(pageable) } returns PageImpl(jobOffersList.filter { it.professional?.id == professionalId })

        val result = jobOfferService.getJobOffers(pageable, null, null, professionalId)

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
                "First job offer",
                JobOffer.Status.CREATED,
                2,
                mutableSetOf(
                    Note(id = 9L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 10L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.EMEA)
                )
            ),
            JobOffer(
                2L,
                "Second job offer",
                "First job offer",
                JobOffer.Status.SELECTION_PHASE,
                2,
                mutableSetOf(
                    Note(id = 1L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 2L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.EMEA)
                )
            )
        )
        every { jobOfferRepository.findAll(pageable) } returns PageImpl(jobOffersList.filter { it.customer.id == customerId && it.status == status })

        val result = jobOfferService.getJobOffers(pageable, customerId, status, null)

        verify{ jobOfferRepository.findAll(pageable) }
        assertEquals(jobOffersList.filter { it.customer.id == customerId && it.status == status}.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetOpenJobOffers_thenReturnJobOfferDTOList(){
        val pageable = PageRequest.of(0, 5)
        val jobOffersSet = mutableSetOf(
            JobOffer(
                1L,
                "First job offer",
                "Second job offer",
                JobOffer.Status.CREATED,
                2,
                mutableSetOf(
                    Note(id = 11L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 12L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.EMEA)
                )
            ),
            JobOffer(
                2L,
                "First job offer",
                "Mobile app development",
                JobOffer.Status.CREATED,
                2,
                mutableSetOf(
                    Note(id = 13L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 14L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.EMEA)
                )
            )
        )
        val customer = Customer(
            id = 1L,
            contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.EMEA),
            jobOffers = jobOffersSet,
            notes = mutableSetOf()
        )



        every { customerRepository.findById(1L) } returns Optional.of(customer)

        val result = jobOfferService.getOpenJobOffers(1L, pageable)

        verify{ customerRepository.findById(1L) }
        assertEquals(jobOffersSet.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetOpenJobOffers_thenReturnCustomerNotFound(){
        val pageable = PageRequest.of(0, 5)
        every { customerRepository.findById(1L) } returns Optional.empty()

        val exception = assertThrows<CustomerNotFoundException> {
            jobOfferService.getOpenJobOffers(1L, pageable)
        }
        assertEquals("Customer with id 1 not found", exception.message)
        verify{ customerRepository.findById(1L) }
    }

    @Test
    fun whenGetAcceptedJobOffers_thenReturnJobOfferDTOList(){
        val pageable = PageRequest.of(0, 5)
        val jobOffersSet = mutableSetOf(
            JobOffer(
                1L,
                "First job offer",
                "Frontend development",
                JobOffer.Status.DONE,
                2,
                mutableSetOf(
                    Note(id = 15L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 16L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.APAC)
                ),
                Professional(
                    id = 1L,
                    contact = Contact(id = 1L, name = "Jane", surname = "Austen", region = Region.APAC),
                    dailyRate = 100.0f
                )
            ),
            JobOffer(
                2L,
                "First job offer",
                "Backend development",
                JobOffer.Status.CONSOLIDATED,
                2,
                mutableSetOf(
                    Note(id = 17L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 18L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.APAC)
                ),
                Professional(
                    id = 2L,
                    contact = Contact(id = 2L, name = "Jane", surname = "Austen", region = Region.APAC),
                    dailyRate = 100.0f
                )
            )
        )

        val professional = Professional(
            id = 1L,
            contact = Contact(id = 1L, name = "Jane", surname = "Austen", region = Region.APAC),
            notes = mutableSetOf(),
            jobOffers = jobOffersSet,
            skills = emptySet(),
        )

        every { professionalRepository.findById(1L) } returns Optional.of(professional)

        val result = jobOfferService.getAcceptedJobOffers(1L, pageable)

        verify{ professionalRepository.findById(1L) }
        assertEquals(jobOffersSet.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetAcceptedJobOffers_thenReturnProfessionalNotFound(){
        val pageable = PageRequest.of(0, 5)
        every { professionalRepository.findById(1L) } returns Optional.empty()

        val exception = assertThrows<ProfessionalNotFoundException> {
            jobOfferService.getAcceptedJobOffers(1L, pageable)
        }
        assertEquals("Professional with id 1 not found", exception.message)
        verify{ professionalRepository.findById(1L) }
    }

    @Test
    fun whenGetAbortedJobOffers_thenReturnJobOfferDTOList(){
        val pageable = PageRequest.of(0, 5)
        val jobOffersSet = mutableSetOf(
            JobOffer(
                1L,
                "First job offer",
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                mutableSetOf(
                    Note(id = 19L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 20L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.APAC),
                ),
                Professional(
                    id = 1L,
                    contact = Contact(id = 1L, name = "Jane", surname = "Austen", region = Region.APAC),
                    dailyRate = 100.0f
                )
            ),
            JobOffer(
                2L,
                "Second job offer",
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                mutableSetOf(
                    Note(id = 21L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 22L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.APAC),
                ),
                Professional(
                    id = 2L,
                    contact = Contact(id = 2L, name = "Jane", surname = "Austen", region = Region.APAC),
                    dailyRate = 100.0f
                )
            )
        )


        every { jobOfferRepository.findAll() } returns jobOffersSet.toList()

        val result = jobOfferService.getAbortedJobOffers(pageable, null, null)

        verify{ jobOfferRepository.findAll() }
        assertEquals(jobOffersSet.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetAbortedJobOffers_thenReturnFilteredByCustomerId(){
        val pageable = PageRequest.of(0, 5)
        val customer = Customer(
            id = 1L,
            contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.LATAM),
            notes = mutableSetOf(),
            jobOffers = mutableSetOf()
        )
        val jobOffersList = listOf(
            JobOffer(
                1L,
                "First job offer",
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                mutableSetOf(
                    Note(id = 24L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 25L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work"),
                customer,
                Professional(
                    id = 1L,
                    contact = Contact(id = 1L, name = "Jane", surname = "Austen", region = Region.LATAM),
                    dailyRate = 100.0f
                )
            ),
            JobOffer(
                2L,
                "First job offer",
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                mutableSetOf(
                    Note(id = 22L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 23L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                )  ,              setOf("smart", "group work", "organised"),
                Customer(
                    id = 2L,
                    contact = Contact(id = 2L, name = "John", surname = "Doe", region = Region.LATAM),
                ),
                Professional(
                    id = 2L,
                    contact = Contact(id = 2L, name = "Jane", surname = "Austen", region = Region.LATAM),
                    dailyRate = 100.0f
                )
            )
        )

        customer.jobOffers.plus(jobOffersList[0])

        every { jobOfferRepository.findAll() } returns jobOffersList
        every { customerRepository.findById(1L) } returns Optional.of(customer)

        val result = jobOfferService.getAbortedJobOffers(pageable, 1L, null)

        verify{ jobOfferRepository.findAll() }
        verify { customerRepository.findById(1L) }
        assertEquals(jobOffersList.filter { it.customer.id == 1L }.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetAbortedJobOffers_thenReturnFilteredByProfessionalId(){
        val pageable = PageRequest.of(0, 5)
        val professional = Professional(
            id = 1L,
            contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.LATAM),
            notes = mutableSetOf(),
            jobOffers = mutableSetOf()
        )
        val jobOffersList = listOf(
            JobOffer(
                1L,
                "First job offer",
                "description",
                JobOffer.Status.ABORTED,
                2,
                mutableSetOf(
                    Note(id = 26L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 27L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work"),
                Customer(
                    id = 1L,
                    contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.LATAM),
                ),
                professional
            ),
            JobOffer(
                2L,
                "First job offer",
                "Project description",
                JobOffer.Status.ABORTED,
                2,
                mutableSetOf(
                    Note(id = 27L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 28L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 2L,
                    contact = Contact(id = 2L, name = "John", surname = "Doe", region = Region.LATAM),
                ),
                Professional(
                    id = 2L,
                    contact = Contact(id = 2L, name = "Jane", surname = "Austen", region = Region.LATAM),
                    dailyRate = 100.0f
                )
            )
        )

        professional.jobOffers.plus(jobOffersList[0])

        every { jobOfferRepository.findAll() } returns jobOffersList
        every { professionalRepository.findById(1L) } returns Optional.of(professional)

        val result = jobOfferService.getAbortedJobOffers(pageable, null, 1L)

        verify{ jobOfferRepository.findAll() }
        verify { professionalRepository.findById(1L) }
        assertEquals(jobOffersList.filter { it.professional?.id == 1L }.map { it.toDTO() }, result)
    }

    @Test
    fun whenGetAbortedJobOffers_thenReturnFilteredByCustomerIdAndProfessionalId(){
        val pageable = PageRequest.of(0, 5)

        val customer = Customer(
            id = 1L,
            contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.LATAM),
            notes = mutableSetOf(),
            jobOffers = mutableSetOf()
        )

        val professional = Professional(
            id = 1L,
            contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.EMEA),
            notes = mutableSetOf(),
            jobOffers = mutableSetOf()
        )

        val jobOffersList = listOf(
            JobOffer(
                1L,
                "First job offer",
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                mutableSetOf(
                    Note(id = 29L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 30L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work"),
                customer,
                professional
            ),
            JobOffer(
                2L,
                "First job offer",
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                mutableSetOf(
                    Note(id = 31L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 32L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work", "organised"),
                Customer(
                    id = 2L,
                    contact = Contact(id = 2L, name = "John", surname = "Doe", region = Region.LATAM),
                ),
                Professional(
                    id = 2L,
                    contact = Contact(id = 2L, name = "Jane", surname = "Austen", region = Region.LATAM),
                    dailyRate = 100.0f
                )
            )
        )

        customer.jobOffers.plus(jobOffersList[0])
        professional.jobOffers.plus(jobOffersList[0])

        every { jobOfferRepository.findAll() } returns jobOffersList
        every { customerRepository.findById(1L) } returns Optional.of(customer)
        every { professionalRepository.findById(1L) } returns Optional.of(professional)

        val result = jobOfferService.getAbortedJobOffers(pageable, 1L, 1L)

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
            jobOfferService.getAbortedJobOffers(pageable, 1L, null)
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
            jobOfferService.getAbortedJobOffers(pageable, null, 1L)
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
            notes = Optional.of(listOf(NoteDTO(id=33L, "good job", LocalDateTime.now(), "prova"))),
        )
        val jobOffer = JobOffer(
            1L,
            "First job offer",
            "First job offer",  // This description stays
            JobOffer.Status.CREATED,
            2,
            mutableSetOf(
                Note(id = 35L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                Note(id = 36L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
            ),
            setOf("smart", "group work"),
            Customer(
                id = 1L,
                contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.LATAM),
            )
        )

        every { jobOfferRepository.findById(jobOfferId) } returns Optional.of(jobOffer)
        every { noteRepository.save(any()) } answers { firstArg() }
        every { jobOfferRepository.save(any()) } answers { firstArg() }

        val result = jobOfferService.updateJobOffer(jobOfferId, jobOfferUpdateDTO)

        verify { jobOfferRepository.findById(jobOfferId) }

        // Assert the actual returned values
        assertEquals(jobOfferId, result.id)
        assertEquals("First job offer", result.title)
        assertEquals("First job offer", result.description)  // Not changed
        assertEquals(JobOffer.Status.SELECTION_PHASE, result.status)
        assertEquals(2, result.duration)
        assertEquals(1, result.notes.size)
        assertEquals(33L, result.notes[0].id)
        assertEquals("good job", result.notes[0].title)
        assertEquals("prova", result.notes[0].description)
        assertEquals(setOf("smart", "group work"), result.requiredSkills)
    }


    @Test
    fun updateJobOffer_thenReturnJobOfferNotFound() {
        val jobOfferId = 100L
        val jobOfferUpdateDTO = JobOfferUpdateDTO(
            status = "SELECTION_PHASE",
            notes = Optional.of(listOf(NoteDTO(47L, "good job", LocalDateTime.now(), "prova" ))),
            professionalId = Optional.of(1L)
        )
        every { jobOfferRepository.findById(jobOfferId) } returns Optional.empty()

        val exception = assertThrows<JobOfferNotFoundException> {
            jobOfferService.updateJobOffer(jobOfferId, jobOfferUpdateDTO)
        }
        verify { jobOfferRepository.findById(jobOfferId)}
        assertEquals("Job offer with id $jobOfferId not found", exception.message)
    }

    @Test
    fun updateJobOffer_thenReturnInvalidStatus() {
        val jobOfferId = 1L
        val jobOfferUpdateDTO = JobOfferUpdateDTO(
            status = "INVALID_STATUS",
            notes = Optional.of(listOf(NoteDTO(48L, "good job", LocalDateTime.now(), "prova" ))),
            professionalId = Optional.of(1L)
        )
        val jobOffer = JobOffer(
            1L,
            "First job offer",
            "First job offer",
            JobOffer.Status.CREATED,
            2,
            mutableSetOf(
                Note(id = 40L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                Note(id = 41L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
            ),
            setOf("smart", "group work"),
            Customer(
                id = 1L,
                contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.LATAM),
            )
        )
        every { jobOfferRepository.findById(jobOfferId) } returns Optional.of(jobOffer)

        val exception = assertThrows<InvalidStatusException> {
            jobOfferService.updateJobOffer(jobOfferId, jobOfferUpdateDTO)
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
            "Description",
            JobOffer.Status.SELECTION_PHASE,
            2,
            mutableSetOf(
                Note(id = 50L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                Note(id = 51L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
            ),
            setOf("smart", "group work"),
            Customer(
                id = 1L,
                contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.LATAM),
            ),
            Professional(
                id = 1L,
                contact = Contact(id = 1L, name = "Jane", surname = "Austen", region = Region.LATAM),
                dailyRate = 100.0f
            ),
            value = 40.0f
        )
        every { jobOfferRepository.findById(jobOfferId) } returns Optional.of(jobOffer)

        val result = jobOfferService.getJobOfferValue(jobOfferId)

        verify { jobOfferRepository.findById(jobOfferId) }
        assertEquals(40.0f, result)
    }

    @Test
    fun getJobOfferValue_thenReturnProfessionalNotAvailable(){
        val jobOfferId = 1L
        val jobOffer = JobOffer(
            1L,
            "First job offer",
            "Description",
            JobOffer.Status.SELECTION_PHASE,
            2,
            mutableSetOf(
                Note(id = 1L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                Note(id = 2L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now()),
                Note(id = 3L, title = "Note 3", description = "Description 2", createdAt = LocalDateTime.now())
            ),
            setOf("smart", "group work"),
            Customer(
                id = 1L,
                contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.LATAM),
            )
        )
        every { jobOfferRepository.findById(jobOfferId) } returns Optional.of(jobOffer)

        val exception = assertThrows<ProfessionalNotAvailableException> {
            jobOfferService.getJobOfferValue(jobOfferId)
        }
        assertEquals("No professional assigned", exception.message)
        verify { jobOfferRepository.findById(jobOfferId) }
    }
}