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
        // Pulisce il DB a ogni avvio (solo per test!)
        // Rimuovi queste righe per dati persistenti
        //messageRepository.deleteAll()
        //jobOfferRepository.deleteAll()
        //customerRepository.deleteAll()
        //professionalRepository.deleteAll()
        //contactRepository.deleteAll()

        // ##################################################
        // ## 1. CONTATTI (CONTACT)
        // ##################################################
        val c1 = Contact(name = "Mario", surname = "Rossi", ssn = "RSSMRA80A01H501U", category = Category.CUSTOMER)
        val c2 = Contact(name = "Anna", surname = "Bianchi", ssn = "BNCANNA75B41L219J", category = Category.CUSTOMER)
        val c3 = Contact(name = "Luigi", surname = "Verdi", ssn = "VRDLGU85C15F205K", category = Category.PROFESSIONAL)
        val c4 = Contact(name = "Sofia", surname = "Neri", ssn = "NRISFO90D50H501X", category = Category.PROFESSIONAL)
        val c5 = Contact(name = "Davide", surname = "Gialli", ssn = "GLLDVD78E22A001Z", category = Category.CUSTOMER)
        val c6 = Contact(name = "Paolo", surname = "Bruno", ssn = null, category = Category.UNKNOWN)

        // ##################################################
        // ## 2. DETTAGLI CONTATTI (EMAIL, ADDRESS, TELEPHONE)
        // ##################################################
        // Vengono aggiunti ai contatti *prima* del salvataggio
        // Questo funziona se le entità Email/Address/Telephone
        // hanno un costruttore che imposta la relazione (es. Email(email=..., contact=...))
        // e Contact ha CascadeType.ALL

        // Contatto 1
        c1.emails = setOf(Email(email = "mario.rossi@example.com", contact = c1))
        c1.addresses = setOf(Address(address = "Via Roma 1, 10123 Torino", contact = c1))
        c1.telephones = setOf(Telephone(telephone = "+39011123456", contact = c1))

        // Contatto 2
        c2.emails = setOf(
            Email(email = "anna.bianchi@example.com", contact = c2),
            Email(email = "a.bianchi@work.com", contact = c2)
        )
        c2.addresses = setOf(Address(address = "Corso Francia 10, 10100 Torino", contact = c2))
        c2.telephones = setOf(Telephone(telephone = "+393331234567", contact = c2))

        // Contatto 3
        c3.emails = setOf(Email(email = "luigi.verdi@gmail.com", contact = c3))
        c3.addresses = setOf(Address(address = "Via Garibaldi 20, 20121 Milano", contact = c3))
        c3.telephones = setOf(Telephone(telephone = "+393478901234", contact = c3))

        // Contatto 4
        c4.emails = setOf(Email(email = "sofia.neri@outlook.com", contact = c4))
        c4.addresses = setOf(Address(address = "Piazza Castello 5, 10122 Torino", contact = c4))
        c4.telephones = setOf(Telephone(telephone = "+393385556677", contact = c4))

        // Contatto 5
        c5.emails = setOf(Email(email = "davide.gialli@startup.it", contact = c5))
        c5.addresses = setOf(Address(address = "Lungo Po Antonelli 15, 10153 Torino", contact = c5))
        c5.telephones = setOf(Telephone(telephone = "+39011987654", contact = c5))

        // Contatto 6
        c6.emails = setOf(Email(email = "paolo.bruno@info.com", contact = c6))

        // Salva tutti i contatti e i loro dettagli (grazie alla Cascade)
        // I repository 'saveAll' restituiscono le entità gestite con gli ID

        val savedContacts = contactRepository.saveAll(listOf(c1, c2, c3, c4, c5, c6))
        val savedC1 = savedContacts[0]
        val savedC2 = savedContacts[1]
        val savedC3 = savedContacts[2]
        val savedC4 = savedContacts[3]
        val savedC5 = savedContacts[4]

        // ##################################################
        // ## 3. CLIENTI (CUSTOMER)
        // ##################################################
        val cust1 = Customer(contact = savedC1)
        cust1.notes = mutableListOf("Cliente storico, molto affidabile", "Preferisce contatto via email")

        val cust2 = Customer(contact = savedC2)
        cust2.notes = mutableListOf("Nuovo cliente, acquisito tramite fiera")

        val cust3 = Customer(contact = savedC5)
        cust3.notes = mutableListOf("Startup innovativa, settore IoT")

        val (savedCust1, savedCust2, savedCust3) =
            customerRepository.saveAll(listOf(cust1, cust2, cust3))

        // ##################################################
        // ## 4. PROFESSIONISTI (PROFESSIONAL)
        // ##################################################
        val prof1 = Professional(
            dailyRate = 300.0F,
            employmentState = Professional.State.AVAILABLE_FOR_WORK, // 1
            location = "Milano",
            contact = savedC3
        )
        prof1.skills = mutableSetOf("Kotlin", "Spring Boot", "JPA/Hibernate", "PostgreSQL")
        prof1.notes = mutableListOf("Senior developer, 10+ anni esperienza")

        val prof2 = Professional(
            dailyRate = 120.0F,
            employmentState = Professional.State.EMPLOYED, // 0
            location = "Torino",
            contact = savedC4
        )
        prof2.skills = mutableSetOf("React", "TypeScript", "Node.js", "UX/UI Design")
        prof2.notes = mutableListOf("Front-end specialist", "Attualmente impiegata, ma valuta offerte")

        val (savedProf1, savedProf2) =
            professionalRepository.saveAll(listOf(prof1, prof2))

        // ##################################################
        // ## 5. OFFERTE DI LAVORO (JOB OFFER)
        // ##################################################
        val jo1 = JobOffer(
            description = "Sviluppo backend microservizi",
            status = JobOffer.Status.CREATED, // 0
            duration = 120,
            customer = savedCust1,
            professional = null
        )
        jo1.notes = mutableListOf("Richiesta urgente")
        jo1.requiredSkills = mutableSetOf("Java", "Spring Boot")

        val jo2 = JobOffer(
            description = "Refactoring interfaccia utente",
            status = JobOffer.Status.CONSOLIDATED, // 3
            duration = 90,
            value = 2160.0F,
            customer = savedCust2,
            professional = savedProf2
        )
        jo2.requiredSkills = mutableSetOf("React", "UX/UI Design")

        val jo3 = JobOffer(
            description = "Migrazione database legacy",
            status = JobOffer.Status.DONE, // 4
            duration = 60,
            value = 1800.0F,
            customer = savedCust1,
            professional = savedProf1
        )
        jo3.requiredSkills = mutableSetOf("PostgreSQL", "Data Migration")

        val jo4 = JobOffer(
            description = "Sviluppo app mobile Android",
            status = JobOffer.Status.SELECTION_PHASE, // 1
            duration = 180,
            customer = savedCust3,
            professional = null
        )
        jo4.notes = mutableListOf("Progetto a lungo termine")
        jo4.requiredSkills = mutableSetOf("Kotlin", "Android SDK")

        val jo5 = JobOffer(
            description = "Consulenza sicurezza",
            status = JobOffer.Status.ABORTED, // 5
            duration = 30,
            customer = savedCust2,
            professional = null
        )
        jo5.notes = mutableListOf("Budget annullato dal cliente")
        jo5.requiredSkills = mutableSetOf("Security")

        jobOfferRepository.saveAll(listOf(jo1, jo2, jo3, jo4, jo5))

        // ##################################################
        // ## 6. MESSAGGI E STORICO (MESSAGE)
        // ##################################################
        val msg1 = Message(
            sender = "mario.rossi@example.com",
            date = LocalDateTime.parse("2025-10-26T10:00:00"),
            subject = "URGENTE: Problema server",
            body = "Il server di produzione è down!",
            channel = Channel.EMAIL, // 2
            state = State.PROCESSING, // 3
            priority = Priority.HIGH // 2
        )
        msg1.history = mutableListOf(
            History(date = LocalDateTime.parse("2025-10-26T10:00:00"), state = State.RECEIVED, comment = "Messaggio ricevuto e parcheggiato", message = msg1),
            History(date = LocalDateTime.parse("2025-10-26T10:05:00"), state = State.READ, comment = "Letto da operatore", message = msg1),
            History(date = LocalDateTime.parse("2025-10-26T10:06:00"), state = State.PROCESSING, comment = "Assegnato a tecnico senior", message = msg1)
        )

        val msg2 = Message(
            sender = "+393331234567",
            date = LocalDateTime.parse("2025-10-27T09:30:00"),
            subject = "Chiamata da Anna Bianchi",
            body = "Chiede info su stato Job Offer #2",
            channel = Channel.PHONE_CALL, // 0
            state = State.DONE, // 4
            priority = Priority.MEDIUM // 1
        )
        msg2.history = mutableListOf(
            History(date = LocalDateTime.parse("2025-10-27T09:30:00"), state = State.RECEIVED, comment = "Chiamata in ingresso", message = msg2),
            History(date = LocalDateTime.parse("2025-10-27T09:35:00"), state = State.DONE, comment = "Richiesta evasa, fornite info su Sofia Neri.", message = msg2)
        )

        val msg3 = Message(
            sender = "+393478901234",
            date = LocalDateTime.parse("2025-10-27T09:45:00"),
            subject = "Disponibilità",
            body = "Confermo disponibilità per nuovi progetti da settimana prossima. Saluti, Luigi Verdi",
            channel = Channel.TEXT_MESSAGE, // 1
            state = State.READ, // 1
            priority = Priority.LOW // 0
        )
        msg3.history = mutableListOf(
            History(date = LocalDateTime.parse("2025-10-27T09:45:00"), state = State.RECEIVED, comment = "SMS da Luigi Verdi", message = msg3),
            History(date = LocalDateTime.parse("2025-10-27T09:50:00"), state = State.READ, comment = "Letto e archiviato", message = msg3)
        )

        messageRepository.saveAll(listOf(msg1, msg2, msg3))
    }
}