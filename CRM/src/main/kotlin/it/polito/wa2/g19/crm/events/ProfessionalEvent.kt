package it.polito.wa2.g19.crm.events

import it.polito.wa2.g19.crm.dtos.ContactDTO
import it.polito.wa2.g19.crm.entities.Professional
import it.polito.wa2.g19.crm.utils.ContactPayload
import it.polito.wa2.g19.crm.dtos.ProfessionalDTO
import it.polito.wa2.g19.crm.dtos.ProfessionalUpdateDTO
import it.polito.wa2.g19.crm.dtos.UpdateContactDTO

enum class ProfessionalEventType {
    CREATED,
    CONTACT_UPDATED,
    NOTES_UPDATED,
    PROFILE_UPDATED,
    DELETED
}

data class ProfessionalEvent(
    val eventType: ProfessionalEventType,

    // Primary Key
    val id: Long,

    //payload
    val contact: ContactPayload? = null,
    val notes: List<String>? = null,
    val skills: List<String>? = null,
    val dailyRate: Float? = null,
    val employmentState: Professional.State? = null,
    val location: String? = null,

    // Metadata
    val changedFields: Set<String> = emptySet(),
    val occurredAt: Long = System.currentTimeMillis()
)

fun ProfessionalDTO.toProfessionalCreatedEvent(): ProfessionalEvent =
    ProfessionalEvent(
        eventType = ProfessionalEventType.CREATED,
        id = this.id,
        contact = this.contact.toPayload(),
        notes = this.notes,
        skills = this.skills.toList(),
        dailyRate = this.dailyRate,
        employmentState = this.employmentState,
        location = this.location,
        changedFields = setOf("contact", "notes", "skills", "dailyRate", "employmentState", "location")
    )

fun UpdateContactDTO.toProfessionalContactUpdatedEvent(customerId: Long, currentContact: ContactDTO): ProfessionalEvent {
    val changedFields = mutableSetOf < String >()
    val merged = currentContact.copy(
        name = this.name ?: currentContact.name,
        surname = this.surname ?: currentContact.surname,
        ssn = this.ssn ?: currentContact.ssn,
    ).also {
        if (this.name != null && this.name != currentContact.name) changedFields += "contact.name"
        if (this.surname != null && this.surname != currentContact.surname) changedFields += "contact.surname"
        if (this.ssn != null && this.ssn != currentContact.ssn) changedFields += "contact.ssn"
    }

    return ProfessionalEvent(
        eventType = ProfessionalEventType.CONTACT_UPDATED,
        id = customerId,
        contact = merged.toPayload(),
        changedFields = if (changedFields.isEmpty()) emptySet() else changedFields.toSet(),
    )
}

fun ProfessionalUpdateDTO.toProfileUpdatedEvent(
    professionalId: Long,
    current: ProfessionalDTO
): ProfessionalEvent {
    val changedFields = mutableSetOf < String >()

    val newNotes = if (notes.isPresent && notes.get() != current.notes) {
        changedFields += "notes"; notes.get()
    } else {
        null
    }

    val newSkills = if (skills.isPresent && skills.get().toSet() != current.skills) {
        changedFields += "skills"; skills.get()
    } else {
        null
    }

    val newDailyRate = if (dailyRate.isPresent && dailyRate.get() != current.dailyRate) {
        changedFields += "dailyRate"; dailyRate.get()
    } else {
        null
    }

    val newEmploymentState = if (employmentState.isPresent && employmentState.get() != current.employmentState) {
        changedFields += "employmentState"; employmentState.get()
    } else {
        null
    }

    val newLocation = if (location.isPresent && location.get() != current.location) {
        changedFields += "location"; location.get()
    } else {
        null
    }

    return ProfessionalEvent(
        eventType = if (changedFields.contains("notes") && changedFields.size == 1)
            ProfessionalEventType.NOTES_UPDATED
        else
            ProfessionalEventType.PROFILE_UPDATED,
        id = professionalId,
        notes = newNotes,
        skills = newSkills?.toList(),
        dailyRate = newDailyRate,
        employmentState = newEmploymentState,
        location = newLocation,
        changedFields = if (changedFields.isEmpty()) emptySet() else changedFields.toSet(),
    )
}

fun ProfessionalDTO.toNotesUpdatedEvent(): ProfessionalEvent =
    ProfessionalEvent(
        eventType = ProfessionalEventType.NOTES_UPDATED,
        id = this.id,
        notes = notes,
        changedFields = setOf("notes")
    )

fun ProfessionalDTO.toDeletedEvent(): ProfessionalEvent =
    ProfessionalEvent(
        eventType = ProfessionalEventType.DELETED,
        id = this.id,
        changedFields = emptySet()
    )