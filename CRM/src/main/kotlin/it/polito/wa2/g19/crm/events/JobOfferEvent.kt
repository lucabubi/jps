package it.polito.wa2.g19.crm.events

import it.polito.wa2.g19.crm.entities.JobOffer.Status
import it.polito.wa2.g19.crm.dtos.JobOfferDTO
import it.polito.wa2.g19.crm.dtos.JobOfferUpdateDTO

enum class JobOfferEventType { CREATED, UPDATED, ABORTED }


data class JobOfferEvent(
    val eventType: JobOfferEventType,

    //primary key
    val id: Long,

    //current status
    val status: Status,

    //references
    val customerId: Long,
    val professionalId: Long? = null,

    //fields "payload" for Debezium
    val description: String? = null,
    val duration: Int? = null,
    val notes: List<String>? = null,
    val requiredSkills: Set<String>? = null,
    val value: Float? = null,

    // metadata
    val changedFields: Set<String> = emptySet(),
    val occurredAt: Long = System.currentTimeMillis()

)

fun JobOfferDTO.toCreatedJobOfferEvent(): JobOfferEvent {
    return JobOfferEvent(
        eventType = JobOfferEventType.CREATED,
        id = this.id,
        status = this.status,
        customerId = this.customer.id,
        professionalId = this.professional?.id,
        description = this.description,
        duration = this.duration,
        notes = this.notes,
        requiredSkills = this.requiredSkills,
        value = this.value,
        changedFields = setOf(
            "description",
            "status",
            "customerId",
            "professionalId",
            "duration",
            "notes",
            "requiredSkills",
            "value"
        )
    )
}

fun JobOfferDTO.toUpdatedJobOfferEvent(updateDTO: JobOfferUpdateDTO, changedFields: Set<String>): JobOfferEvent {
    return JobOfferEvent(
        eventType = JobOfferEventType.UPDATED,
        id = this.id,
        status = Status.valueOf(updateDTO.status),
        customerId = this.customer.id,
        professionalId = updateDTO.professionalId.orElse(this.professional?.id),
        description = this.description,
        duration = this.duration,
        notes = updateDTO.notes.orElse(this.notes),
        requiredSkills = this.requiredSkills,
        value = this.value,
        changedFields = changedFields
    )
}

fun JobOfferDTO.toAbortedJobOfferEvent(): JobOfferEvent {
    return JobOfferEvent(
        eventType = JobOfferEventType.ABORTED,
        id = this.id,
        status = Status.ABORTED,
        customerId = this.customer.id,
        professionalId = this.professional?.id,
        description = this.description,
        duration = this.duration,
        notes = this.notes,
        requiredSkills = this.requiredSkills,
        value = this.value,
        changedFields = setOf("status")
    )
}