package it.polito.wa2.g19.analytics.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("job_offers")
data class JobOfferDocument(
    @Id val id: Long,
    var description: String? = null,
    var status: String? = null,
    var duration: Int? = null,
    var notes: List<String>? = null,
    var requiredSkills: Set<String>? = null,
    var customerId: Long? = null,
    var professionalId: Long? = null,
    var value: Float? = null,

    var lastEventType: String? = null,
    var lastChangedFields: Set<String>? = null,
    var lastOccurredAt: Long? = null
)
