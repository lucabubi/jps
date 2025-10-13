package it.polito.wa2.g19.analytics.dtos

data class JobOfferViewDTO(
    val id: Long,
    val description: String?,
    val status: String?,
    val duration: Int?,
    val notes: List<String>?,
    val requiredSkills: Set<String>?,
    val customerId: Long,
    val professionalId: Long?,
    val value: Float?,
    val lastEventType: String?,
    val lastChangedFields: Set<String>?,
    val lastOccurredAt: Long?
)
