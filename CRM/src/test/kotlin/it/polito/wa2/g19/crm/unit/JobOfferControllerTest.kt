package it.polito.wa2.g19.crm.unit

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import it.polito.wa2.g19.crm.controllers.JobOfferController
import it.polito.wa2.g19.crm.dtos.*
import it.polito.wa2.g19.crm.entities.*
import it.polito.wa2.g19.crm.exceptions.CustomerNotFoundException
import it.polito.wa2.g19.crm.exceptions.ProfessionalNotFoundException
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.domain.PageRequest
import it.polito.wa2.g19.crm.services.JobOfferService
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.util.*

@ActiveProfiles("h2")
@WebMvcTest(
    controllers = [JobOfferController::class],
    excludeFilters = [ComponentScan.Filter(type = FilterType.ANNOTATION, classes = [EnableWebSecurity::class])]
)
@AutoConfigureMockMvc(addFilters = false)
class JobOfferControllerTest(
    @param:Autowired val mockMvc: MockMvc,
    @param:Autowired val objectMapper: ObjectMapper
) {
    @MockkBean
    lateinit var jobOfferService: JobOfferService

    @Test
    fun testGetJobOffers(){
        val page = 0
        val size = 10
        val customerId = 1L
        val status = JobOffer.Status.CREATED
        val professionalId = 1L

        val jobOffersDTOlist = listOf(
            JobOfferDTO(
                1L,
                "First job offer",
                "Description of the first job offer",
                JobOffer.Status.CREATED,
                2,
                listOf(
                    NoteDTO(id = 1L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 2L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),                setOf("smart", "group work"),
                CustomerMinimalDTO(
                    id = 1L,
                    contact = ContactDTO(id = 1L, name = "John", surname = "Doe", region = Region.NA)
                )
            ),
            JobOfferDTO(
                2L,
                "First job offer",
                "Description of the first job offer",
                JobOffer.Status.CREATED,
                2,
                listOf(
                    NoteDTO(id = 3L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 4L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work", "organised"),
                CustomerMinimalDTO(
                    id = 1L,
                    contact = ContactDTO(id = 1L, name = "John", surname = "Doe", region = Region.NA)
                )
            )
        )

        every { jobOfferService.getJobOffers(any(), any(), any(), any()) } returns jobOffersDTOlist

        mockMvc.perform(
            get("/API/joboffers/")
            .param("page", page.toString())
            .param("size", size.toString())
            .param("customerId", customerId.toString())
            .param("status", status.toString())
            .param("professionalId", professionalId.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(jobOffersDTOlist)))
    }

    @Test
    fun testGetOpenJobOffers_statusOk(){
        val pageable = PageRequest.of(0, 10)

        val customer = Customer(
            id = 1L,
            contact = Contact(
                id = 1L,
                name = "John",
                surname = "Doe",
                category = Category.CUSTOMER,
                emails = mutableSetOf(),
                addresses = mutableSetOf(),
                telephones = mutableSetOf(),
                region = Region.NA,
                notes = mutableSetOf(
                        Note(id = 3L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                        Note(id = 4L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                )
            ),
            jobOffers = mutableSetOf()
        )

        val jobOffersSet = mutableSetOf(
            JobOffer(
                id = 1L,
                description = "First job offer",
                title = "First job offer",
                status = JobOffer.Status.CREATED,
                value = 2.0F,
                notes = mutableSetOf(
                    Note(id = 5L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 6L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                requiredSkills = setOf("smart", "group work"),
                customer = customer
            ),
            JobOffer(
                id = 2L,
                title = "Second job offer",
                description = "First job offer",
                status = JobOffer.Status.CREATED,
                value = 2.0F,
                notes = mutableSetOf(
                    Note(id = 7L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 8L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                requiredSkills = setOf("smart", "group work", "organised"),
                customer = customer
            )
        )

        customer.jobOffers = jobOffersSet

        every { jobOfferService.getOpenJobOffers(customer.id, pageable) } returns jobOffersSet.map { it.toDTO() }

        mockMvc.perform(
            get("/API/joboffers/open/${customer.id}")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(
                content().json(objectMapper.writeValueAsString(jobOffersSet.map { it.toDTO() }))
            )

    }

    @Test
    fun testGetOpenJobOffers_statusNotFound(){
        val pageable = PageRequest.of(0, 10)
        val customerId = 1L

        every { jobOfferService.getOpenJobOffers(customerId, pageable) } throws CustomerNotFoundException("Customer with id: $customerId not found")

        mockMvc.perform(
            get("/API/joboffers/open/$customerId")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun testGetAcceptedJobOffers_statusOk(){
        val pageable = PageRequest.of(0, 10)

        val professional = Professional(
            id = 1L,
            contact = Contact(
                id = 1L,
                name = "John",
                surname = "Doe",
                category = Category.PROFESSIONAL,
                emails = mutableSetOf(),
                addresses = mutableSetOf(),
                telephones = mutableSetOf(),
                region = Region.NA,
                notes = mutableSetOf(
                    Note(id = 9L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 10L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                )
            ),
            jobOffers = mutableSetOf()
        )

        val jobOffersSet = mutableSetOf(
            JobOffer(
                id = 1L,
                title = "First job offer",
                description = "First job offer",
                status = JobOffer.Status.CONSOLIDATED,
                value = 2.0F,
                notes = mutableSetOf(
                    Note(id = 11L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 12L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                requiredSkills = setOf("smart", "group work"),
                customer = Customer(
                    id = 1L,
                    contact = Contact(
                        id = 1L,
                        name = "John",
                        surname = "Doe",
                        category = Category.CUSTOMER,
                        emails = mutableSetOf(),
                        addresses = mutableSetOf(),
                        telephones = mutableSetOf(),
                        region = Region.NA,
                        notes = mutableSetOf(
                            Note(id = 13L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                            Note(id = 14L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                        )
                    ),
                    jobOffers = mutableSetOf()
                )
            ),
            JobOffer(
                id = 2L,
                title = "Second job offer",
                description = "First job offer",
                status = JobOffer.Status.DONE,
                value = 2.0F,
                notes = mutableSetOf(
                    Note(id = 15L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 16L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                requiredSkills = setOf("smart", "group work", "organised"),
                customer = Customer(
                    id = 2L,
                    contact = Contact(
                        id = 2L,
                        name = "Jane",
                        surname = "Doe",
                        category = Category.CUSTOMER,
                        emails = mutableSetOf(),
                        addresses = mutableSetOf(),
                        telephones = mutableSetOf(),
                        region = Region.NA,
                        notes = mutableSetOf(
                            Note(id = 17L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                            Note(id = 18L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                        )
                    ),
                    jobOffers = mutableSetOf()

                ),
                professional = professional
            )
        )

        professional.jobOffers = jobOffersSet

        every { jobOfferService.getAcceptedJobOffers(professional.id, pageable) } returns jobOffersSet.map { it.toDTO() }

        mockMvc.perform(
            get("/API/joboffers/accepted/${professional.id}")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(
                content().json(objectMapper.writeValueAsString(jobOffersSet.map { it.toDTO() }))
            )
    }

    @Test
    fun testGetAcceptedJobOffers_statusNotFound(){
        val pageable = PageRequest.of(0, 10)
        val professionalId = 1L

        every { jobOfferService.getAcceptedJobOffers(professionalId, pageable) } throws ProfessionalNotFoundException("Professional with id: $professionalId not found")

        mockMvc.perform(
            get("/API/joboffers/accepted/$professionalId")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun getAbortedJobOffers_statusOk(){
        val pageable = PageRequest.of(0, 10)
        val jobOffers = listOf(
            JobOfferDTO(
                1L,
                "JobOffer: ",
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf(
                    NoteDTO(id = 19L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 20L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work"),
                CustomerMinimalDTO(
                    id = 1L,
                    contact = ContactDTO(id = 1L, name = "John", surname = "Doe", region = Region.NA),
                )
            ),
            JobOfferDTO(
                2L,
                "JobOffer: ",
                "Second job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf(
                    NoteDTO(id = 21L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 22L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work", "organised"),
                CustomerMinimalDTO(
                    id = 1L,
                    contact = ContactDTO(id = 1L, name = "John", surname = "Doe", region = Region.NA),
                )
            )
        )

        every { jobOfferService.getAbortedJobOffers(pageable, null, null) } returns jobOffers

        mockMvc.perform(
            get("/API/joboffers/aborted/")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(
                content().json(objectMapper.writeValueAsString(jobOffers))
            )
    }

    @Test
    fun getAbortedJobOffersFilteredByCustomer_statusOk(){
        val pageable = PageRequest.of(0, 10)

        val customer = Customer(
            id = 1L,
            contact = Contact(
                id = 1L,
                name = "John",
                surname = "Doe",
                category = Category.CUSTOMER,
                emails = mutableSetOf(),
                addresses = mutableSetOf(),
                telephones = mutableSetOf(),
                region = Region.NA,
                notes = mutableSetOf(
                    Note(id = 23L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 24L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                )
            ),
            jobOffers = mutableSetOf()
        )

        val jobOffers = listOf(
            JobOfferDTO(
                1L,
                "JobOffer: ",
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf(
                    NoteDTO(id = 25L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 26L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work"),
                CustomerMinimalDTO(
                    id = 1L,
                    contact = ContactDTO(id = 1L, name = "John", surname = "Doe", region = Region.NA),
                )
            ),
            JobOfferDTO(
                2L,
                "JobOffer: ",
                "Second job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf(
                    NoteDTO(id = 27L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 28L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work", "organised"),
                CustomerMinimalDTO(
                    id = 2L,
                    contact = ContactDTO(id = 2L, name = "Jane", surname = "Doe", region = Region.NA),
                )
            )
        )

        every { jobOfferService.getAbortedJobOffers(pageable, customer.id, null) } returns jobOffers.filter { it.customer.id == customer.id }

        val expectedResult = jobOffers.filter { it.customer.id == customer.id }

        mockMvc.perform(
            get("/API/joboffers/aborted/")
            .param("page", "0")
            .param("size", "10")
            .param("customerId", 1L.toString()))
            .andExpect(status().isOk)
            .andExpect(
                content().json(objectMapper.writeValueAsString(expectedResult))
            )
    }

    @Test
    fun testGetAbortedJobOffersFilteredByCustomer_statusNotFound(){
        val pageable = PageRequest.of(0, 10)
        val customerId = 1L

        every { jobOfferService.getAbortedJobOffers(pageable, customerId, null) } throws CustomerNotFoundException("Customer with id: $customerId not found")

        mockMvc.perform(
            get("/API/joboffers/aborted/")
            .param("page", "0")
            .param("size", "10")
            .param("customerId", customerId.toString()))
            .andExpect(status().isNotFound)
    }

    @Test
    fun testGetAbortedJobOffersFilteredByProfessional_statusOk(){
        val pageable = PageRequest.of(0, 10)
        val professional = Professional(
            id = 1L,
            contact = Contact(
                id = 1L,
                name = "John",
                surname = "Doe",
                category = Category.PROFESSIONAL,
                emails = mutableSetOf(),
                addresses = mutableSetOf(),
                telephones = mutableSetOf(),
                region = Region.NA,
                notes = mutableSetOf(
                    Note(id = 29L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 30L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                )
            ),
            skills = emptySet(),
            dailyRate = 100f,
            employmentState = Professional.State.AVAILABLE_FOR_WORK,
        )

        val jobOffers = listOf(
            JobOfferDTO(
                1L,
                "JobOffer: ",
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf(
                    NoteDTO(id = 1L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 2L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),
                setOf("smart", "group work"),
                CustomerMinimalDTO(
                    id = 1L,
                    contact = ContactDTO(id = 1L, name = "John", surname = "Doe", region = Region.NA),
                ),
                professional.toDTO()
            ),
            JobOfferDTO(
                2L,
                "JobOffer: ",
                "Second job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf(
                    NoteDTO(id = 31L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 32L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),                setOf("smart", "group work", "organised"),
                CustomerMinimalDTO(
                    id = 2L,
                    contact = ContactDTO(id = 2L, name = "Jane", surname = "Doe", region = Region.NA),
                ),
                ProfessionalDTO(
                    id = 2L,
                    contact = ContactDTO(id = 2L, name = "Jane", surname = "Doe", region = Region.NA)

                )
            )
        )

        professional.jobOffers = mutableSetOf(jobOffers[0].toEntity())

        every { jobOfferService.getAbortedJobOffers(pageable, null, professional.id) } returns jobOffers.filter { it.professional?.id == professional.id }

        val expectedResult = jobOffers.filter { it.professional?.id == professional.id }

        mockMvc.perform(
            get("/API/joboffers/aborted/")
            .param("page", "0")
            .param("size", "10")
            .param("professionalId", professional.id.toString()))
            .andExpect(status().isOk)
            .andExpect(
                content().json(objectMapper.writeValueAsString(expectedResult))
            )
    }

    @Test
    fun testGetAbortedJobOffersFilteredByProfessional_statusNotFound(){
        val pageable = PageRequest.of(0, 10)
        val professionalId = 1L

        every { jobOfferService.getAbortedJobOffers(pageable, null, professionalId) } throws ProfessionalNotFoundException("Professional with id: $professionalId not found")

        mockMvc.perform(
            get("/API/joboffers/aborted/")
            .param("page", "0")
            .param("size", "10")
            .param("professionalId", professionalId.toString()))
            .andExpect(status().isNotFound)
    }

    @Test
    fun testGetAbortedJobOffersFilteredByCustomerProfessional_statusOk(){
        val pageable = PageRequest.of(0, 10)
        val customer = Customer(
            id = 1L,
            contact = Contact(
                id = 1L,
                name = "John",
                surname = "Doe",
                category = Category.CUSTOMER,
                emails = mutableSetOf(),
                addresses = mutableSetOf(),
                telephones = mutableSetOf(),
                region = Region.NA,
                notes = mutableSetOf(
                    Note(id = 33L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 34L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                )
            ),
            jobOffers = mutableSetOf()
        )
        val professional = Professional(
            id = 1L,
            contact = Contact(
                id = 1L,
                name = "John",
                surname = "Doe",
                category = Category.PROFESSIONAL,
                emails = mutableSetOf(),
                addresses = mutableSetOf(),
                telephones = mutableSetOf(),
                region = Region.NA,
                notes = mutableSetOf(
                    Note(id = 35L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 36L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                )
            ),
            skills = emptySet(),
            dailyRate = 100f,
            employmentState = Professional.State.AVAILABLE_FOR_WORK,
        )

        val jobOffers = listOf(
            JobOfferDTO(
                1L,
                "JobOffer: ",
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf(
                    NoteDTO(id = 37L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 38L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),                setOf("smart", "group work"),
                CustomerMinimalDTO(
                    id = 1L,
                    contact = ContactDTO(id = 1L, name = "John", surname = "Doe", region = Region.NA),
                ),
                professional.toDTO()
            ),
            JobOfferDTO(
                2L,
                "JobOffer: ",
                "Second job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf(
                    NoteDTO(id = 39L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 40L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                ),                setOf("smart", "group work", "organised"),
                CustomerMinimalDTO(
                    id = 2L,
                    contact = ContactDTO(id = 2L, name = "Jane", surname = "Doe", region = Region.NA),
                ),
                ProfessionalDTO(
                    id = 2L,
                    contact = ContactDTO(id = 3L, name = "Jane", surname = "Doe", region = Region.NA)

                )
            )
        )

        professional.jobOffers = mutableSetOf(jobOffers[0].toEntity())
        customer.jobOffers = mutableSetOf(jobOffers[0].toEntity())

        every { jobOfferService.getAbortedJobOffers(pageable, customer.id, professional.id) } returns jobOffers.filter { it.professional?.id == professional.id && it.customer.id == customer.id }

        val expectedResult = jobOffers.filter { it.professional?.id == professional.id && it.customer.id == customer.id }

        mockMvc.perform(
            get("/API/joboffers/aborted/")
            .param("page", "0")
            .param("size", "10")
            .param("customerId", customer.id.toString())
            .param("professionalId", professional.id.toString()))
            .andExpect(status().isOk)
            .andExpect(
                content().json(objectMapper.writeValueAsString(expectedResult))
            )
    }

    @Test
    fun testChangeJobOfferStatus() {
        val joboffersId = 1L
        val date = LocalDateTime.now()
        val jobOfferUpdateDTO = JobOfferUpdateDTO(
            status = "SELECTION_PHASE",
            notes = Optional.of(listOf(
                NoteDTO(id = 41L, title = "Note 1", description = "Description 1", createdAt = date),
                NoteDTO(id = 42L, title = "Note 2", description = "Description 2", createdAt = date)
            )),
            professionalId = Optional.of(1L)
        )

        every { jobOfferService.updateJobOffer(any(), any()) } returns JobOfferDTO(
            1L,
            "JobOffer: ",
            "First job offer",
            JobOffer.Status.SELECTION_PHASE,
            2,
            listOf(
                NoteDTO(id = 1L, title = "Note 1", description = "Description 1", createdAt = date),
            ),
            setOf("smart", "group work"),
            CustomerMinimalDTO(
                id = 1L,
                contact = ContactDTO(id = 1L, name = "John", surname = "Doe", region = Region.NA)
            ),
            ProfessionalDTO(
                id = 1L,
                contact = ContactDTO(id = 1L, name = "John", surname = "Doe", region = Region.NA)
            )
        )

        mockMvc.perform(
            post("/API/joboffers/$joboffersId")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(jobOfferUpdateDTO))
        )
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    objectMapper.writeValueAsString(
                        JobOfferDTO(
                            1L,
                            "JobOffer: ",
                            "First job offer",
                            JobOffer.Status.SELECTION_PHASE,
                            2,
                            listOf(
                                NoteDTO(id = 1L, title = "Note 1", description = "Description 1", createdAt = date),
                            ),
                            setOf("smart", "group work"),
                            CustomerMinimalDTO(
                                id = 1L,
                                contact = ContactDTO(id = 1L, name = "John", surname = "Doe", region = Region.NA)
                            ),
                            ProfessionalDTO(
                                id = 1L,
                                contact = ContactDTO(id = 1L, name = "John", surname = "Doe", region = Region.NA)
                            )
                        )
                    )
                )
            )
    }

    @Test
    fun testGetJobOfferValue(){
        val jobOfferId = 1L
        val jobOfferValue = 100f
        every { jobOfferService.getJobOfferValue(any()) } returns jobOfferValue
        mockMvc.perform(get("/API/joboffers/$jobOfferId/value"))
            .andExpect(status().isOk)
            .andExpect(content().string(jobOfferValue.toString()))
    }

}