package it.polito.wa2.g19.analytics.events

data class CustomerEvent(
    val id: Long,
    val fullName: String?,
    val email: String?,
    val phone: String?,                 // tienilo piatto per semplicità
    val tags: Set<String>? = null,      // opzionale, utile per analitiche

    override val eventType: String,
    override val occurredAt: Long? = null,
    override val changedFields: Set<String>? = null
) : BaseEvent
