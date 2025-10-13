package it.polito.wa2.g19.analytics.dtos

data class CustomerViewDTO(
    val id: Long,
    val fullName: String?,
    val email: String?,
    val phone: String?,
    val tags: Set<String>?,
    val lastEventType: String?,
    val lastChangedFields: Set<String>?,
    val lastOccurredAt: Long?
)
