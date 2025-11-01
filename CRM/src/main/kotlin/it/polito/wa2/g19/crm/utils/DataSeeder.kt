package it.polito.wa2.g19.crm.utils

import it.polito.wa2.g19.crm.entities.*
import it.polito.wa2.g19.crm.repositories.*
import jakarta.transaction.Transactional
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DataSeeder(
    private val contactRepository: ContactRepository,
    private val customerRepository: CustomerRepository,
    private val professionalRepository: ProfessionalRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val messageRepository: MessageRepository

) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {

        // ##################################################
        // ## 1. CONTACTS
        // ##################################################
        val c1 = Contact(name = "Mario", surname = "Rossi", ssn = "RSSMRA80A01H501U", region= Region.EMEA, category = Category.CUSTOMER)
        val c2 = Contact(name = "Anna", surname = "Bianchi", ssn = "BNCANNA75B41L219J", region = Region.EMEA, category = Category.CUSTOMER)
        val c3 = Contact(name = "Luigi", surname = "Verdi", ssn = "VRDLGU85C15F205K", region = Region.NA, category = Category.PROFESSIONAL)
        val c4 = Contact(name = "Sofia", surname = "Neri", ssn = "NRISFO90D50H501X", region = Region.NA, category = Category.PROFESSIONAL)
        val c5 = Contact(name = "Davide", surname = "Gialli", ssn = "GLLDVD78E22A001Z", region = Region.APAC, category = Category.CUSTOMER)
        val c6 = Contact(name = "Paolo", surname = "Bruno", ssn = null, region = Region.LATAM, category = Category.UNKNOWN)

        // ##################################################
        // ## 2. CONTACT DETAILS
        // ##################################################

        // Contact 1
        c1.emails = mutableSetOf(Email(email = "mario.rossi@example.com", contact = c1))
        c1.addresses = mutableSetOf(Address(address = "Via Roma, 1", zipCode = "10123", city = "Torino", country = "Italy", contact = c1))
        c1.telephones = mutableSetOf(Telephone(telephone = "+39011123456", contact = c1))

        // Contact 2
        c2.emails = mutableSetOf(
            Email(email = "anna.bianchi@example.com", contact = c2),
            Email(email = "a.bianchi@work.com", contact = c2)
        )
        c2.addresses = mutableSetOf(Address(address = "Corso Francia, 10", zipCode = "10100", city = "Torino", country = "Italy", contact = c2))
        c2.telephones = mutableSetOf(Telephone(telephone = "+393331234567", contact = c2))

        // Contact 3
        c3.emails = mutableSetOf(Email(email = "luigi.verdi@gmail.com", contact = c3))
        c3.addresses = mutableSetOf(Address(contact = c3, city = "New York", country = "USA", address = "789 Broadway Ave.", zipCode = "10003"))
        c3.telephones = mutableSetOf(Telephone(telephone = "+393478901234", contact = c3))

        // Contact 4
        c4.emails = mutableSetOf(Email(email = "sofia.neri@outlook.com", contact = c4))
        c4.addresses = mutableSetOf(Address(contact = c4, city = "Toronto", country = "Canada", address = "456 Maple Rd.", zipCode = "M4B1B4"))
        c4.telephones = mutableSetOf(Telephone(telephone = "+393385556677", contact = c4))

        // Contact 5
        c5.emails = mutableSetOf(Email(email = "davide.gialli@startup.it", contact = c5))
        c5.addresses = mutableSetOf(Address(contact = c5, city = "Cairo", country = "Egypt", address = "123 Nile St.", zipCode = "11511"))
        c5.telephones = mutableSetOf(Telephone(telephone = "+39011987654", contact = c5))

        // Contact 6
        c6.emails = mutableSetOf(Email(email = "paolo.bruno@info.com", contact = c6))
        c6.addresses = mutableSetOf(Address(address = "Av. Siempre Viva 742", city = "Buenos Aires", zipCode = "C1000", country = "Argentina", contact = c6))


        val savedContacts = contactRepository.saveAll(listOf(c1, c2, c3, c4, c5, c6))
        val savedC1 = savedContacts[0]
        val savedC2 = savedContacts[1]
        val savedC3 = savedContacts[2]
        val savedC4 = savedContacts[3]
        val savedC5 = savedContacts[4]

        // ##################################################
        // ## 3. CUSTOMER
        // ##################################################
        val cust1 = Customer(contact = savedC1)
        cust1.contact.notes = mutableSetOf(
            Note(title = "Old customer", description = "", createdAt = LocalDateTime.now(), contact = cust1.contact),
            Note(title = "Prefers to be contacted via email", description = "", createdAt = LocalDateTime.now(), contact = cust1.contact)
        )
        val cust2 = Customer(contact = savedC2)
        cust2.contact.notes = mutableSetOf(
            Note(title = "Customer Info", description = "New customer, interested in web development services.", createdAt = LocalDateTime.now(), contact = cust2.contact)
        )
        val cust3 = Customer(contact = savedC5)
        cust3.contact.notes = mutableSetOf(
            Note(title = "Company Info", description = "Innovative Startup, IoT Industry", createdAt = LocalDateTime.now(), contact = cust3.contact)
        )

        val (savedCust1, savedCust2, savedCust3) =
            customerRepository.saveAll(listOf(cust1, cust2, cust3))

        // ##################################################
        // ## 4. PROFESSIONALS
        // ##################################################
        val prof1 = Professional(
            dailyRate = 300.0F,
            employmentState = Professional.State.AVAILABLE_FOR_WORK, // 1
            contact = savedC3
        )
        prof1.skills = mutableSetOf("Kotlin", "Spring Boot", "JPA/Hibernate", "PostgreSQL")
        prof1.contact.notes = mutableSetOf(
            Note(title = "Experience", description = "Senior developer, 10+ years experience", createdAt = LocalDateTime.now(), contact = prof1.contact)
        )
        val prof2 = Professional(
            dailyRate = 120.0F,
            employmentState = Professional.State.EMPLOYED, // 0
            contact = savedC4
        )
        prof2.skills = mutableSetOf("React", "TypeScript", "Node.js", "UX/UI Design")
        prof2.contact.notes = mutableSetOf(
            Note(title = "Specialization", description = "Front-end specialist", createdAt = LocalDateTime.now(), contact = prof2.contact),
            Note(title = "Employment Status", description = "Actually employed, considers offers", createdAt = LocalDateTime.now(), contact = prof2.contact)
        )

        val (savedProf1, savedProf2) =
            professionalRepository.saveAll(listOf(prof1, prof2))

        // ##################################################
        // ## 5. Job Offers
        // ##################################################
        val jo1 = JobOffer(
            title = "Backend Developer",
            description = "Microservices backend development",
            status = JobOffer.Status.CREATED, // 0
            duration = 120,
            customer = savedCust1,
            professional = null
        )
        jo1.notes = mutableSetOf(
            Note(title = "Priority", description = "URGENT", createdAt = LocalDateTime.now(), jobOffer = jo1)
        )
        jo1.requiredSkills = mutableSetOf("Java", "Spring Boot")

        val jo2 = JobOffer(
            title = "Frontend Developer",
            description = "Refactoring interfaccia utente",
            status = JobOffer.Status.CONSOLIDATED, // 3
            duration = 90,
            value = 2160.0F,
            customer = savedCust2,
            professional = savedProf2
        )
        jo2.requiredSkills = mutableSetOf("React", "UX/UI Design")

        val jo3 = JobOffer(
            title = "Database Migration Specialist",
            description = "Legacy data migration to PostgreSQL",
            status = JobOffer.Status.DONE, // 4
            duration = 60,
            value = 1800.0F,
            customer = savedCust1,
            professional = savedProf1
        )
        jo3.requiredSkills = mutableSetOf("PostgreSQL", "Data Migration")

        val jo4 = JobOffer(
            title = "Mobile App Developer",
            description = "Android app development",
            status = JobOffer.Status.SELECTION_PHASE, // 1
            duration = 180,
            customer = savedCust3,
            professional = null
        )
        jo4.notes = mutableSetOf(
            Note(title = "Project Duration", description = "Long-term project", createdAt = LocalDateTime.now(), jobOffer = jo4),
            Note(title = "Skill Requirement", description = "Looking for Kotlin expert", createdAt = LocalDateTime.now(), jobOffer = jo4)
        )
        jo4.requiredSkills = mutableSetOf("Kotlin", "Android SDK")

        val jo5 = JobOffer(
            title = "Security Auditor",
            description = "Security audit and compliance",
            status = JobOffer.Status.ABORTED, // 5
            duration = 30,
            customer = savedCust2,
            professional = null
        )
        jo5.notes = mutableSetOf(
            Note(title = "Budget", description = "Budget constraints", createdAt = LocalDateTime.now(), jobOffer = jo5)
        )
        jo5.requiredSkills = mutableSetOf("Security")

        jobOfferRepository.saveAll(listOf(jo1, jo2, jo3, jo4, jo5))

        // ##################################################
        // ## 6. MESSAGES AND MESSAGE HISTORY
        // ##################################################
        val msg1 = Message(
            sender = "mario.rossi@example.com",
            date = LocalDateTime.parse("2025-10-26T10:00:00"),
            subject = "URGENT: Server Down",
            body = "Production server is down since 9 AM. Immediate assistance required!",
            channel = Channel.EMAIL, // 2
            state = State.PROCESSING, // 3
            priority = Priority.HIGH // 2
        )
        msg1.history = mutableListOf(
            History(date = LocalDateTime.parse("2025-10-26T10:00:00"), state = State.RECEIVED, comment = "Message received", message = msg1),
            History(date = LocalDateTime.parse("2025-10-26T10:05:00"), state = State.READ, comment = "Read by operator 1", message = msg1),
            History(date = LocalDateTime.parse("2025-10-26T10:06:00"), state = State.PROCESSING, comment = "Assigned to senior technician", message = msg1)
        )

        val msg2 = Message(
            sender = "+393331234567",
            date = LocalDateTime.parse("2025-10-27T09:30:00"),
            subject = "Call from Anna Bianchi",
            body = "Asks for information about job offer #2",
            channel = Channel.PHONE_CALL, // 0
            state = State.DONE, // 4
            priority = Priority.MEDIUM // 1
        )
        msg2.history = mutableListOf(
            History(date = LocalDateTime.parse("2025-10-27T09:30:00"), state = State.RECEIVED, comment = "Incoming call", message = msg2),
            History(date = LocalDateTime.parse("2025-10-27T09:35:00"), state = State.DONE, comment = "Request processed, info given about Sofia Neri.", message = msg2)
        )

        val msg3 = Message(
            sender = "+393478901234",
            date = LocalDateTime.parse("2025-10-27T09:45:00"),
            subject = "Availability for new projects",
            body = "I confirm my availability for new projects starting next month. Luigi Verdi",
            channel = Channel.TEXT_MESSAGE, // 1
            state = State.READ, // 1
            priority = Priority.LOW // 0
        )
        msg3.history = mutableListOf(
            History(date = LocalDateTime.parse("2025-10-27T09:45:00"), state = State.RECEIVED, comment = "SMS from Luigi Verdi", message = msg3),
            History(date = LocalDateTime.parse("2025-10-27T09:50:00"), state = State.READ, comment = "Read and archived", message = msg3)
        )

        messageRepository.saveAll(listOf(msg1, msg2, msg3))
    }
}