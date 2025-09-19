package it.polito.wa2.g19.crm.dtos

import it.polito.wa2.g19.crm.entities.Professional
import it.polito.wa2.g19.crm.entities.Professional.State

data class ProfessionalDTO(
    val id : Long = 0,
    val contact: ContactDTO,
    val notes: List<String> = emptyList(),
    val skills: Set<String> = emptySet(),
    var dailyRate: Float = 0.0f,
    var employmentState: State = State.AVAILABLE_FOR_WORK,
    var location: String? = null
){
    fun toEntity() : Professional {
        return Professional(
            id = this.id,
            contact = this.contact.toEntity(),
            notes = this.notes,
            skills = this.skills,
            dailyRate = this.dailyRate,
            employmentState = this.employmentState,
            location = this.location
        )
    }
}