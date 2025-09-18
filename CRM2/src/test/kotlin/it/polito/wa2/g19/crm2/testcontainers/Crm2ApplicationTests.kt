package it.polito.wa2.g19.crm2.testcontainers

import it.polito.wa2.g19.crm2.dtos.*
import it.polito.wa2.g19.crm2.entities.Category
import it.polito.wa2.g19.crm2.exceptions.*
import it.polito.wa2.g19.crm2.repositories.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class Crm2ApplicationTests {

    @Autowired
    private lateinit var contactRepository: ContactRepository

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var professionalRepository: ProfessionalRepository

    @Autowired
    private lateinit var jobOfferRepository: JobOfferRepository


    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    companion object {
        @Container
        val postgres: PostgreSQLContainer<Nothing> = PostgreSQLContainer<Nothing>("postgres:latest")

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    private val customerDTO1 = CustomerDTO(
        contact = ContactDTO(
            name = "John",
            surname = "Doe",
            category = Category.CUSTOMER,
            emails = emptySet(),
            addresses = emptySet(),
            telephones = emptySet(),
        ),
        notes = listOf("Note 1", "Note 2"),
        jobOffers = emptySet()
    )
    private val customerDTO2 = CustomerDTO(
        contact = ContactDTO(
            name = "John",
            surname = "Austen",
            category = Category.CUSTOMER,
            emails = emptySet(),
            addresses = emptySet(),
            telephones = emptySet(),
        ),
        notes = listOf("Note", "Additional note"),
        jobOffers = emptySet()
    )
    private val professionalDTO1 = ProfessionalDTO(
        contact = ContactDTO(
            name = "John1",
            surname = "Doe1",
            category = Category.PROFESSIONAL,
            emails = emptySet(),
            addresses = emptySet(),
            telephones = emptySet(),
        ),
        notes = listOf("Note 1", "Note 2"),
        skills = setOf("Skill 1", "Skill 2"),
        location = "Milan"
    )
    private val professionalDTO2 = ProfessionalDTO(
        contact = ContactDTO(
            name = "Jane1",
            surname = "Austen1",
            category = Category.PROFESSIONAL,
            emails = emptySet(),
            addresses = emptySet(),
            telephones = emptySet(),
        ),
        notes = listOf("Note 1", "Note 2"),
        skills = setOf("Skill", "Another skill"),
        location = "Turin"
    )

    private val createJobOfferDTO = CreateJobOfferDTO(
        description = "Description 1",
        notes = listOf("Note 1", "Note 2"),
        customerId = 1L
    )
    private val jobOfferDTO = JobOfferDTO(
        description = createJobOfferDTO.description,
        duration = createJobOfferDTO.duration,
        notes = createJobOfferDTO.notes,
        requiredSkills = createJobOfferDTO.requiredSkills,
        customer = CustomerMinimalDTO(
            id = customerDTO1.id,
            contact = customerDTO1.contact,
            notes = customerDTO1.notes
        )
    )
    private val APIURL = "http://localhost:8080/API/"


    @BeforeEach
    fun setUp() {
        jobOfferRepository.deleteAll()
        professionalRepository.deleteAll()
        customerRepository.deleteAll()
        contactRepository.deleteAll()
        customerRepository.saveAll(listOf(customerDTO1.toEntity(), customerDTO2.toEntity()))
        professionalRepository.saveAll(listOf(professionalDTO1.toEntity(), professionalDTO2.toEntity()))
        jobOfferRepository.saveAll(listOf(jobOfferDTO.toEntity()))
    }

    @Test
    fun testCreateCustomer_status200() {


        val response = restTemplate.postForEntity<CustomerDTO>(APIURL + "customers/", customerDTO1, CustomerDTO::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)

    }

    @Test
    fun testCreateProfessional_status200() {
        val response = restTemplate.postForEntity<ProfessionalDTO>(APIURL + "professionals/", professionalDTO1, ProfessionalDTO::class.java)


        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetCustomer_status200() {
        val response = restTemplate.getForEntity(APIURL + "customers/1", CustomerDTO::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetCustomer_status404() {
        assertThrows<Exception> {
            val response = restTemplate.getForEntity(APIURL + "customers/10", CustomerNotFoundException::class.java)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }
    }

    @Test
    fun testGetProfessional_status200() {
        val response = restTemplate.getForEntity(APIURL + "professionals/1", ProfessionalDTO::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetProfessional_status404() {
        assertThrows<Exception> {
            val response = restTemplate.getForEntity(APIURL + "professionals/10", ProfessionalNotFoundException::class.java)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }
    }

    @Test
    fun testGetCustomers_status200() {
        val response = restTemplate.getForEntity(APIURL + "customers/", List::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetProfessionals_status200() {
        val response = restTemplate.getForEntity(APIURL + "professionals/", List::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetOpenJobOffers_status200() {
        val response = restTemplate.getForEntity(APIURL + "joboffers/open/1", List::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetOpenJobOffers_status404() {
        assertThrows<Exception> {
            val response = restTemplate.getForEntity(APIURL + "joboffers/open/10", CustomerNotFoundException::class.java)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }
    }

    @Test
    fun testGetAcceptedJobOffers_status200() {
        val response = restTemplate.getForEntity(APIURL + "joboffers/accepted/1", List::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetAcceptedJobOffers_status404() {
        assertThrows<Exception> {
            val response = restTemplate.getForEntity(APIURL + "joboffers/accepted/10", ProfessionalNotFoundException::class.java)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }
    }

    @Test
    fun testGetAbortedJobOffers_status200() {
        val response = restTemplate.getForEntity(APIURL + "joboffers/aborted/", List::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetAbortedJobOffersFilterByCustomer_status200() {
        val response = restTemplate.getForEntity(APIURL + "joboffers/aborted/?customerId=1", List::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetAbortedJobOffersFilteredByProfessional_status200(){
        val response = restTemplate.getForEntity(APIURL + "joboffers/aborted/?professionalId=1", List::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetAbortedJobOffersFilteredByCustomerAndProfessional_status200(){
        val response = restTemplate.getForEntity(APIURL + "joboffers/aborted/?customerId=1&professionalId=1", List::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetAbortedJobOffersFiltered_status404(){
        val response = restTemplate.getForEntity(APIURL + "joboffers/aborted/?customerId=10", String::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun testGetJobOffers_status200() {
        val response = restTemplate.getForEntity(APIURL + "joboffers/", List::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetJobOffersFilteredByStatus_status200() {
        val response = restTemplate.getForEntity(APIURL + "joboffers/?status=DONE", List::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetJobOffersFilteredByStatus_status404() {
        assertThrows<Exception> {
            val response = restTemplate.getForEntity(APIURL + "joboffers/?status=NOT_EXISTING", InvalidStatusException::class.java)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }
    }

    @Test
    fun testGetJobOffersFilteredByCustomer_status200() {
        val response = restTemplate.getForEntity(APIURL + "joboffers/?customerId=1", List::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetJobOffersFilteredByProfessional_status200() {
        val response = restTemplate.getForEntity(APIURL + "joboffers/?professionalId=1", List::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetJobOffersFilteredByCustomerAndProfessional_status200() {
        val response = restTemplate.getForEntity(APIURL + "joboffers/?customerId=1&professionalId=1", List::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetJobOfferValue_status200() {
        val response = restTemplate.getForEntity(APIURL + "joboffers/1/value", Float::class.java)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun testGetJobOfferValue_status404() {
        assertThrows<Exception> {
            val response = restTemplate.getForEntity(APIURL + "joboffers/10/value", JobOfferNotFoundException::class.java)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }
    }


    @Test
    fun testChangeJobOfferStatus_status409(){
        assertThrows<Exception> {
            val response = restTemplate.postForEntity(APIURL + "joboffers/1", JobOfferUpdateDTO("CREATED"), JobOfferNotFoundException::class.java)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }
    }
}
