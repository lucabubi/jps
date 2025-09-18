package it.polito.wa2.g19.crm2.unit
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.verify
import it.polito.wa2.g19.crm2.dtos.*
import it.polito.wa2.g19.crm2.entities.*
import it.polito.wa2.g19.crm2.exceptions.*
import it.polito.wa2.g19.crm2.services.CRM2Service
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest
class CRM2ControllerTests(@Autowired val mockMvc: MockMvc, @Autowired val objectMapper: ObjectMapper) {

    @MockkBean
    lateinit var crm2Service: CRM2Service

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
        every { crm2Service.createCustomer(customerDTO) } returns customerDTO

        // Act & Assert
        mockMvc.perform(post("/API/customers/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(customerDTO)))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(customerDTO)))

        verify { crm2Service.createCustomer(customerDTO) }
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
            location = "Turin"
        )
        every { crm2Service.createProfessional(professionalDTO) } returns professionalDTO

        // Act & Assert
        mockMvc.perform(post("/API/professionals/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(professionalDTO)))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(professionalDTO)))

        verify { crm2Service.createProfessional(professionalDTO) }
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
        every { crm2Service.getCustomers() } returns customers

        // Act & Assert
        mockMvc.perform(get("/API/customers/"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(customers)))

        verify { crm2Service.getCustomers() }
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
        every { crm2Service.getCustomer(id) } returns customerDTO

        // Act & Assert
        mockMvc.perform(get("/API/customers/$id"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(customerDTO)))

        verify { crm2Service.getCustomer(id) }
    }

    @Test
    fun whenGetCustomer_thenThrowCustomerNotFoundException() {
        // Arrange
        val id = 1L
        every { crm2Service.getCustomer(id) } throws CustomerNotFoundException("Customer with id: $id not found")

        // Act & Assert
        mockMvc.perform(get("/API/customers/$id"))
            .andExpect(status().isNotFound)
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
                telephones= emptySet(),),
            notes = listOf("Note 1", "Note 2"),
            skills = emptySet(),
            dailyRate = 100f,
            employmentState = Professional.State.AVAILABLE_FOR_WORK,
            location = "Turin"
        )
        every { crm2Service.getProfessional(id) } returns professionalDTO

        // Act & Assert
        mockMvc.perform(get("/API/professionals/$id"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(professionalDTO)))

        verify { crm2Service.getProfessional(id) }
    }

    @Test
    fun whenGetProfessional_thenThrowProfessionalNotFoundException() {
        // Arrange
        val id = 1L
        every { crm2Service.getProfessional(id) } throws ProfessionalNotFoundException("Professional with id: $id not found")

        // Act & Assert
        mockMvc.perform(get("/API/professionals/$id"))
            .andExpect(status().isNotFound)
    }

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
                JobOffer.Status.CREATED,
                2,
                listOf("good", "fine"),
                setOf("smart", "group work"),
                CustomerMinimalDTO(
                    id = 1L,
                    contact = ContactDTO(id = 1L, name = "John", surname = "Doe")
                )
            ),
            JobOfferDTO(
                2L,
                "First job offer",
                JobOffer.Status.CREATED,
                2,
                listOf("nice", "call again"),
                setOf("smart", "group work", "organised"),
                CustomerMinimalDTO(
                    id = 1L,
                    contact = ContactDTO(id = 1L, name = "John", surname = "Doe")
                )
            )
        )

        every { crm2Service.getJobOffers(any(), any(), any(), any()) } returns jobOffersDTOlist

        mockMvc.perform(get("/API/joboffers/")
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
                emails = emptySet(),
                addresses = emptySet(),
                telephones = emptySet()
            ),
            notes = listOf("Note 1", "Note 2"),
            jobOffers = emptySet()
        )

        val jobOffersSet = setOf(
            JobOffer(
                id = 1L,
                description = "First job offer",
                status = JobOffer.Status.CREATED,
                value = 2.0F,
                notes = listOf("good", "fine"),
                requiredSkills = setOf("smart", "group work"),
                customer = customer
            ),
            JobOffer(
                id = 2L,
                description = "First job offer",
                status = JobOffer.Status.CREATED,
                value = 2.0F,
                notes = listOf("nice", "call again"),
                requiredSkills = setOf("smart", "group work", "organised"),
                customer = customer
            )
        )

        customer.jobOffers = jobOffersSet

        every { crm2Service.getOpenJobOffers(customer.id, pageable) } returns jobOffersSet.map { it.toDTO() }

        mockMvc.perform(get("/API/joboffers/open/${customer.id}")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(jobOffersSet.map { it.toDTO() }))
        )

    }

    @Test
    fun testGetOpenJobOffers_statusNotFound(){
        val pageable = PageRequest.of(0, 10)
        val customerId = 1L

        every { crm2Service.getOpenJobOffers(customerId, pageable) } throws CustomerNotFoundException("Customer with id: $customerId not found")

        mockMvc.perform(get("/API/joboffers/open/$customerId")
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
                emails = emptySet(),
                addresses = emptySet(),
                telephones = emptySet()
            ),
            notes = listOf("Note 1", "Note 2"),
            jobOffers = emptySet()
        )

        val jobOffersSet = setOf(
            JobOffer(
                id = 1L,
                description = "First job offer",
                status = JobOffer.Status.CONSOLIDATED,
                value = 2.0F,
                notes = listOf("good", "fine"),
                requiredSkills = setOf("smart", "group work"),
                customer = Customer(
                    id = 1L,
                    contact = Contact(
                        id = 1L,
                        name = "John",
                        surname = "Doe",
                        category = Category.CUSTOMER,
                        emails = emptySet(),
                        addresses = emptySet(),
                        telephones = emptySet()
                    ),
                    notes = listOf("Note 1", "Note 2"),
                    jobOffers = emptySet()
                )
            ),
            JobOffer(
                id = 2L,
                description = "First job offer",
                status = JobOffer.Status.DONE,
                value = 2.0F,
                notes = listOf("nice", "call again"),
                requiredSkills = setOf("smart", "group work", "organised"),
                customer = Customer(
                    id = 2L,
                    contact = Contact(
                        id = 2L,
                        name = "Jane",
                        surname = "Doe",
                        category = Category.CUSTOMER,
                        emails = emptySet(),
                        addresses = emptySet(),
                        telephones = emptySet()
                    ),
                    notes = listOf("Note 1", "Note 2"),
                    jobOffers = emptySet()

                ),
                professional = professional
            )
        )

        professional.jobOffers = jobOffersSet

        every { crm2Service.getAcceptedJobOffers(professional.id, pageable) } returns jobOffersSet.map { it.toDTO() }

        mockMvc.perform(get("/API/joboffers/accepted/${professional.id}")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(jobOffersSet.map { it.toDTO() }))
        )
    }

    @Test
    fun testGetAcceptedJobOffers_statusNotFound(){
        val pageable = PageRequest.of(0, 10)
        val professionalId = 1L

        every { crm2Service.getAcceptedJobOffers(professionalId, pageable) } throws ProfessionalNotFoundException("Professional with id: $professionalId not found")

        mockMvc.perform(get("/API/joboffers/accepted/$professionalId")
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
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("good", "fine"),
                setOf("smart", "group work"),
                CustomerMinimalDTO(
                    id = 1L,
                    contact = ContactDTO(id = 1L, name = "John", surname = "Doe")
                )
            ),
            JobOfferDTO(
                2L,
                "Second job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("nice", "call again"),
                setOf("smart", "group work", "organised"),
                CustomerMinimalDTO(
                    id = 1L,
                    contact = ContactDTO(id = 1L, name = "John", surname = "Doe")
                )
            )
        )

        every { crm2Service.getAbortedJobOffers(pageable, null, null) } returns jobOffers

        mockMvc.perform(get("/API/joboffers/aborted/")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(jobOffers))
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
                emails = emptySet(),
                addresses = emptySet(),
                telephones = emptySet()
            ),
            notes = listOf("Note 1", "Note 2"),
            jobOffers = emptySet()
        )

        val jobOffers = listOf(
            JobOfferDTO(
                1L,
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("good", "fine"),
                setOf("smart", "group work"),
                CustomerMinimalDTO(
                    id = 1L,
                    contact = ContactDTO(id = 1L, name = "John", surname = "Doe")
                )
            ),
            JobOfferDTO(
                2L,
                "Second job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("nice", "call again"),
                setOf("smart", "group work", "organised"),
                CustomerMinimalDTO(
                    id = 2L,
                    contact = ContactDTO(id = 2L, name = "Jane", surname = "Doe")
                )
            )
        )

        every { crm2Service.getAbortedJobOffers(pageable, customer.id, null) } returns jobOffers.filter { it.customer.id == customer.id }

        val expectedResult = jobOffers.filter { it.customer.id == customer.id }

        mockMvc.perform(get("/API/joboffers/aborted/")
            .param("page", "0")
            .param("size", "10")
            .param("customerId", 1L.toString()))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResult))
            )
    }

    @Test
    fun testGetAbortedJobOffersFilteredByCustomer_statusNotFound(){
        val pageable = PageRequest.of(0, 10)
        val customerId = 1L

        every { crm2Service.getAbortedJobOffers(pageable, customerId, null) } throws CustomerNotFoundException("Customer with id: $customerId not found")

        mockMvc.perform(get("/API/joboffers/aborted/")
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
                emails = emptySet(),
                addresses = emptySet(),
                telephones = emptySet()
            ),
            notes = listOf("Note 1", "Note 2"),
            skills = emptySet(),
            dailyRate = 100f,
            employmentState = Professional.State.AVAILABLE_FOR_WORK,
            location = "Turin"
        )

        val jobOffers = listOf(
            JobOfferDTO(
                1L,
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("good", "fine"),
                setOf("smart", "group work"),
                CustomerMinimalDTO(
                    id = 1L,
                    contact = ContactDTO(id = 1L, name = "John", surname = "Doe")
                ),
                professional.toDTO()
            ),
            JobOfferDTO(
                2L,
                "Second job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("nice", "call again"),
                setOf("smart", "group work", "organised"),
                CustomerMinimalDTO(
                    id = 2L,
                    contact = ContactDTO(id = 2L, name = "Jane", surname = "Doe")
                ),
                ProfessionalDTO(
                    id = 2L,
                    contact = ContactDTO(id = 2L, name = "Jane", surname = "Doe")

                )
            )
        )

        professional.jobOffers = setOf(jobOffers[0].toEntity())

        every { crm2Service.getAbortedJobOffers(pageable, null, professional.id) } returns jobOffers.filter { it.professional?.id == professional.id }

        val expectedResult = jobOffers.filter { it.professional?.id == professional.id }

        mockMvc.perform(get("/API/joboffers/aborted/")
            .param("page", "0")
            .param("size", "10")
            .param("professionalId", professional.id.toString()))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResult))
        )
    }

    @Test
    fun testGetAbortedJobOffersFilteredByProfessional_statusNotFound(){
        val pageable = PageRequest.of(0, 10)
        val professionalId = 1L

        every { crm2Service.getAbortedJobOffers(pageable, null, professionalId) } throws ProfessionalNotFoundException("Professional with id: $professionalId not found")

        mockMvc.perform(get("/API/joboffers/aborted/")
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
                emails = emptySet(),
                addresses = emptySet(),
                telephones = emptySet()
            ),
            notes = listOf("Note 1", "Note 2"),
            jobOffers = emptySet()
        )
        val professional = Professional(
            id = 1L,
            contact = Contact(
                id = 1L,
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
            employmentState = Professional.State.AVAILABLE_FOR_WORK,
            location = "Turin"
        )

        val jobOffers = listOf(
            JobOfferDTO(
                1L,
                "First job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("good", "fine"),
                setOf("smart", "group work"),
                CustomerMinimalDTO(
                    id = 1L,
                    contact = ContactDTO(id = 1L, name = "John", surname = "Doe")
                ),
                professional.toDTO()
            ),
            JobOfferDTO(
                2L,
                "Second job offer",
                JobOffer.Status.ABORTED,
                2,
                listOf("nice", "call again"),
                setOf("smart", "group work", "organised"),
                CustomerMinimalDTO(
                    id = 2L,
                    contact = ContactDTO(id = 2L, name = "Jane", surname = "Doe")
                ),
                ProfessionalDTO(
                    id = 2L,
                    contact = ContactDTO(id = 3L, name = "Jane", surname = "Doe")

                )
            )
        )

        professional.jobOffers = setOf(jobOffers[0].toEntity())
        customer.jobOffers = setOf(jobOffers[0].toEntity())

        every { crm2Service.getAbortedJobOffers(pageable, customer.id, professional.id) } returns jobOffers.filter { it.professional?.id == professional.id && it.customer.id == customer.id }

        val expectedResult = jobOffers.filter { it.professional?.id == professional.id && it.customer.id == customer.id }

        mockMvc.perform(get("/API/joboffers/aborted/")
            .param("page", "0")
            .param("size", "10")
            .param("customerId", customer.id.toString())
            .param("professionalId", professional.id.toString()))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResult))
        )
    }

    @Test
    fun testChangeJobOfferStatus() {
        val joboffersId = 1L
        val jobOfferUpdateDTO = JobOfferUpdateDTO(
            status = "SELECTION_PHASE",
            notes = Optional.of(listOf("good job")),
            professionalId = Optional.of(1L)
        )

        every { crm2Service.updateJobOffer(any(), any()) } returns JobOfferDTO(
            1L,
            "First job offer",
            JobOffer.Status.SELECTION_PHASE,
            2,
            listOf("good job"),
            setOf("smart", "group work"),
            CustomerMinimalDTO(
                id = 1L,
                contact = ContactDTO(id = 1L, name = "John", surname = "Doe")
            ),
            ProfessionalDTO(
                id = 1L,
                contact = ContactDTO(id = 1L, name = "John", surname = "Doe")
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
                            "First job offer",
                            JobOffer.Status.SELECTION_PHASE,
                            2,
                            listOf("good job"),
                            setOf("smart", "group work"),
                            CustomerMinimalDTO(
                                id = 1L,
                                contact = ContactDTO(id = 1L, name = "John", surname = "Doe")
                            ),
                            ProfessionalDTO(
                                id = 1L,
                                contact = ContactDTO(id = 1L, name = "John", surname = "Doe")
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
        every { crm2Service.getJobOfferValue(any()) } returns jobOfferValue
        mockMvc.perform(get("/API/joboffers/$jobOfferId/value"))
            .andExpect(status().isOk)
            .andExpect(content().string(jobOfferValue.toString()))
    }
}