package it.polito.wa2.g19.crm.unit

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import it.polito.wa2.g19.crm.dtos.ContactDTO
import it.polito.wa2.g19.crm.dtos.NoteDTO
import it.polito.wa2.g19.crm.dtos.ProfessionalDTO
import it.polito.wa2.g19.crm.dtos.ProfessionalUpdateDTO
import it.polito.wa2.g19.crm.entities.*
import it.polito.wa2.g19.crm.exceptions.ProfessionalNotAvailableException
import it.polito.wa2.g19.crm.exceptions.ProfessionalNotFoundException
import it.polito.wa2.g19.crm.repositories.ProfessionalRepository
import it.polito.wa2.g19.crm.services.ProfessionalServiceImpl
import it.polito.wa2.g19.crm.repositories.NoteRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import java.util.*

class ProfessionalServiceTests {
    private val professionalRepository: ProfessionalRepository = mockk()
    private val noteRepository: NoteRepository = mockk()
    private val professionalService =
        ProfessionalServiceImpl(
            professionalRepository,
            noteRepository
        )


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
                telephones= emptySet(),
                region = Region.NA,
                notes = setOf(
                    NoteDTO(id = 1L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    NoteDTO(id = 2L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                    )
                ),
            skills = emptySet(),
            dailyRate = 100f,
            employmentState = Professional.State.AVAILABLE_FOR_WORK,
        )
        val professionalSlot = slot<Professional>()
        val noteSlot = slot<Note>()
        every { professionalRepository.save(capture(professionalSlot)) } answers { professionalSlot.captured }
        every { noteRepository.save(capture(noteSlot)) } answers { noteSlot.captured }  // Add this line


        // Act
        val result = professionalService.createProfessional(professionalDTO)
        val capturedNotes = professionalSlot.captured.contact.notes.sortedBy { it.id }
        val expectedNotes = professionalDTO.contact.notes.map { it.toEntity() }.sortedBy { it.id }
        // Assert
        verify { professionalRepository.save(any()) }
        assertEquals(professionalDTO, result)
        assertEquals(professionalDTO.contact, professionalSlot.captured.contact.toDTO())
        assertEquals(expectedNotes.size, capturedNotes.size)
        capturedNotes.zip(expectedNotes).forEach { (captured, expected) ->
            assertEquals(expected.title, captured.title)
            assertEquals(expected.description, captured.description)
        }
        assertEquals(professionalDTO.skills, professionalSlot.captured.skills)
        assertEquals(professionalDTO.dailyRate, professionalSlot.captured.dailyRate)
        assertEquals(professionalDTO.employmentState, professionalSlot.captured.employmentState)
    }

    @Test
    fun whenUpdateProfessional_thenReturnProfessionalDTO() {
        //mock data
        val professionalId = 1L
        val professionalUpdateDTO = ProfessionalUpdateDTO(
            dailyRate = Optional.of(200f),
            employmentState = Optional.of(Professional.State.AVAILABLE_FOR_WORK),
        )
        val contactDTO = ContactDTO(
            id = 1L,
            name = "John",
            surname = "Doe",
            category = Category.PROFESSIONAL,
            emails = emptySet(),
            addresses = emptySet(),
            telephones = emptySet(),
            region = Region.NA,
            notes = setOf(
                NoteDTO(id = 3L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                NoteDTO(id = 4L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
            )
        )
        val professionalDTO = ProfessionalDTO(
            id = professionalId,
            contact = contactDTO,
            skills = emptySet(),
            dailyRate = 100f,
            employmentState = Professional.State.AVAILABLE_FOR_WORK,
        )
        //given
        every { professionalRepository.findById(professionalId) } returns Optional.of(professionalDTO.toEntity())
        every { professionalRepository.save(any()) } answers { firstArg() }
        //when
        val result = professionalService.updateProfessional(professionalId, professionalUpdateDTO)
        //then
        verify { professionalRepository.findById(professionalId) }
        verify { professionalRepository.save(any()) }
        assertEquals(professionalUpdateDTO.dailyRate.get(), result.dailyRate)
        assertEquals(professionalUpdateDTO.employmentState.get(), result.employmentState)
    }

    @Test
    fun whenUpdateProfessional_thenProfessionalNotAvailableException() {
        val professionalId = 1L
        val professionalUpdateDTO = ProfessionalUpdateDTO(
            dailyRate = Optional.of(200f),
            employmentState = Optional.of(Professional.State.AVAILABLE_FOR_WORK),
        )
        //given
        every { professionalRepository.findById(professionalId) } returns Optional.of(
            Professional(
                id = professionalId,
                contact = Contact(
                    name = "John",
                    surname = "Doe",
                    category = Category.PROFESSIONAL,
                    emails = mutableSetOf(),
                    addresses = mutableSetOf(),
                    telephones = mutableSetOf(),
                    region = Region.NA,
                    notes = mutableSetOf(
                        Note(id = 5L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                        Note(id = 6L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                    )
                ),
                skills = emptySet(),
                dailyRate = 100f,
                employmentState = Professional.State.EMPLOYED,
                jobOffers = mutableSetOf(
                    JobOffer(
                        1L,
                        "Important job",
                        "First job offer",
                        JobOffer.Status.CONSOLIDATED,
                        2,
                        mutableSetOf(
                            Note(id = 7L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                            Note(id = 8L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                        ),                        setOf("smart", "group work"),
                        Customer(
                            id = 1L,
                            contact = Contact(id = 1L, name = "John", surname = "Doe", region = Region.NA),
                        ),
                        Professional(
                            id = 1L,
                            contact = Contact(id = 1L, name = "Jane", surname = "Austen", region = Region.NA),
                            dailyRate = 100.0f
                        ),
                        value = 40.0f
                    )
                )
            )
        )
        //when
        val exception = assertThrows<ProfessionalNotAvailableException> {
            professionalService.updateProfessional(professionalId, professionalUpdateDTO)
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
        )
        //given
        every { professionalRepository.findById(professionalId) } returns Optional.empty()
        //when
        val exception = assertThrows<ProfessionalNotFoundException> {
            professionalService.updateProfessional(professionalId, professionalUpdateDTO)
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
            telephones = emptySet(),
            region = Region.NA,
            notes = setOf(
                NoteDTO(id = 9L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                NoteDTO(id = 10L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
            )
        )
        val contactDTO2 = ContactDTO(
            id = 2L,
            name = "Luca",
            surname = "Barbato",
            category = Category.PROFESSIONAL,
            emails = emptySet(),
            addresses = emptySet(),
            telephones = emptySet(),
            region = Region.NA,
            notes = setOf(
                NoteDTO(id = 11L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                NoteDTO(id = 12L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
            )
        )
        val professionalDTO1 = ProfessionalDTO(
            id = 1L,
            contact = contactDTO1,
        )
        val professionalDTO2 = ProfessionalDTO(
            id = 2L,
            contact = contactDTO2,
        )
        val professionals = listOf(professionalDTO1.toEntity(), professionalDTO2.toEntity())
        val pageable = PageRequest.of(0, 5)
        //given
        every { professionalRepository.findAll(pageable) } returns PageImpl(professionals)
        //when
        val result = professionalService.getProfessionals(pageable, Optional.empty(), Optional.empty(), Optional.empty())
        //then
        verify { professionalRepository.findAll(pageable) }
        assertEquals(professionals.map { it.toDTO() }, result)
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
                emails= mutableSetOf(),
                addresses= mutableSetOf(),
                telephones= mutableSetOf(),
                region = Region.NA,
                notes = mutableSetOf(
                    Note(id = 13L, title = "Note 1", description = "Description 1", createdAt = LocalDateTime.now()),
                    Note(id = 14L, title = "Note 2", description = "Description 2", createdAt = LocalDateTime.now())
                )
            ),
            skills = emptySet(),
            dailyRate = 100f,
            employmentState = Professional.State.AVAILABLE_FOR_WORK,
        )
        every { professionalRepository.findById(id) } returns Optional.of(professional)

        // Act
        val result = professionalService.getProfessional(id)

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
            professionalService.getProfessional(id)
        }
    }

}