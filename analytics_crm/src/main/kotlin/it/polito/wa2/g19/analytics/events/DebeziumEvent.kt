package it.polito.wa2.g19.analytics.events

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

// Ignoriamo i campi che non ci servono (come "schema")
@JsonIgnoreProperties(ignoreUnknown = true)
data class DebeziumEvent<T>(
    val payload: DebeziumPayload<T>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DebeziumPayload<T>(
    val before: T?, // Stato della riga PRIMA
    val after: T?,  // Stato della riga DOPO
    val op: String?,  // 'c', 'u', 'd', 'r'
    @param:JsonProperty("ts_ms")
    val tsMs: Long? // Timestamp
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CustomerPayload(
    val id: Long,
    val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val tags: Set<String>? = null,
    val notes: List<String>? = null
    // Aggiungi altri campi se presenti nella tabella Postgres
)

/**
 * Questo DTO mappa i campi della tua TABELLA POSTGRES.
 * Assicurati che i nomi dei campi corrispondano (es. "customerId" o "customer_id"?)
 * Usa @JsonProperty se i nomi non corrispondono (es. @JsonProperty("customer_id"))
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class JobOfferPayload(
    val id: Long,
    val description: String?,
    val status: String?,
    val duration: Int?,
    val notes: List<String>?,
    val requiredSkills: Set<String>?,
    val customerId: Long,
    val professionalId: Long?,
    val value: Float?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProfessionalPayload(
    val id: Long,
    val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val skills: Set<String>? = null,
    val rating: Float? = null,
    val location: String? = null,
    val dailyRate: Float? = null,
    val employmentState: String? = null,
    val notes: List<String>? = null
)