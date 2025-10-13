package it.polito.wa2.g19.analytics.events

interface BaseEvent {
    val eventType: String
    val occurredAt: Long?
    val changedFields: Set<String>?
}