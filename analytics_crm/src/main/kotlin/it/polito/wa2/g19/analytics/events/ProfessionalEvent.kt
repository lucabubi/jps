package it.polito.wa2.g19.analytics.events

data class ProfessionalEvent(
    val id: Long,
    val fullName: String?,
    val email: String?,
    val phone: String?,
    val skills: Set<String>? = null,
    val rating: Float? = null,

    override val eventType: String,
    override val occurredAt: Long? = null,
    override val changedFields: Set<String>? = null
) : BaseEvent
