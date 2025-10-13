package it.polito.wa2.g19.analytics.events

data class JobOfferEvent(
    val id: Long,
    val description: String?,
    val status: String?,        // usa lo stesso nome dei tuoi Status in CRM (stringa)
    val duration: Int?,
    val notes: List<String>?,
    val requiredSkills: Set<String>?,
    val customerId: Long,
    val professionalId: Long?,  // può essere null
    val value: Float?,

    override val eventType: String,
    override val occurredAt: Long? = null,
    override val changedFields: Set<String>? = null
) : BaseEvent
