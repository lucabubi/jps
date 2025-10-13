package it.polito.wa2.g19.analytics.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("customers")
data class CustomerDocument(
    @Id
    val id: Long,

    var fullName: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var tags: Set<String>? = null,         // eventuali label, categorie o note analitiche
    var notes: List<String>? = null,       // se invii anche le note del CRM

    var lastEventType: String? = null,
    var lastChangedFields: Set<String>? = null,
    var lastOccurredAt: Long? = null
)
