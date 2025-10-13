package it.polito.wa2.g19.analytics.dtos

data class ProfessionalViewDTO(
    val id: Long,
    val fullName: String?,
    val email: String?,
    val phone: String?,
    val skills: Set<String>?,
    val rating: Float?,
    val lastEventType: String?,
    val lastChangedFields: Set<String>?,
    val lastOccurredAt: Long?
)
