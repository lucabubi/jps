package it.polito.wa2.g19.analytics.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("professionals")
data class ProfessionalDocument(
    @Id
    val id: Long,

    var fullName: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var skills: Set<String>? = null,
    var rating: Float? = null,
    var location: String? = null,
    var dailyRate: Float? = null,
    var employmentState: String? = null,
    var notes: List<String>? = null,

    var lastEventType: String? = null,
    var lastChangedFields: Set<String>? = null,
    var lastOccurredAt: Long? = null
)
