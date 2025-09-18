package it.polito.wa2.g19.crm2.dtos

import it.polito.wa2.g19.crm2.entities.JobOffer
import it.polito.wa2.g19.crm2.entities.JobOffer.Status

data class JobOfferDTO(
    val id: Long = 0,
    val description: String = "",
    val status: Status = Status.CREATED,
    val duration: Int = 0,
    val notes: List<String> = emptyList(),
    val requiredSkills: Set<String> = emptySet(),
    var customer: CustomerMinimalDTO,
    var professional : ProfessionalDTO? = null,
    var value: Float? = null
) {
    fun toEntity() : JobOffer {
        return JobOffer(
            id = this.id,
            description = this.description,
            status = this.status,
            duration = this.duration,
            notes = this.notes,
            requiredSkills = this.requiredSkills,
            customer = this.customer.toEntity(),
            professional = this.professional?.toEntity(),
            value = this.value
        )
    }
}
