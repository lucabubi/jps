package it.polito.wa2.g19.crm.events

import it.polito.wa2.g19.crm.utils.ContactPayload
import it.polito.wa2.g19.crm.dtos.CustomerDTO
import it.polito.wa2.g19.crm.dtos.UpdateContactDTO
import it.polito.wa2.g19.crm.dtos.ContactDTO


enum class CustomerEventType {
    CREATED,
    CONTACT_UPDATED,
    NOTES_UPDATED,
    DELETED
}


data class CustomerEvent(
    val eventType: CustomerEventType,

    // Primary Key
    val id: Long,

    //payload
    val contact: ContactPayload? = null,
    val notes: List<String>? = null,

    // Metadata
    val changedFields: Set<String> = emptySet(),
    val occurredAt: Long = System.currentTimeMillis()

    )

fun CustomerDTO.toCreatedCustomerEvent(): CustomerEvent =
    CustomerEvent(
        eventType = CustomerEventType.CREATED,
        id = this.id,
        contact = this.contact.toPayload(),
        notes = this.notes,
        changedFields = setOf("contact", "notes")
    )

fun CustomerDTO.toCustomerNotesUpdatedEvent(newNotes: List<String>): CustomerEvent =
    CustomerEvent(
        eventType = CustomerEventType.NOTES_UPDATED,
        id = this.id,
        notes = newNotes,
        changedFields = setOf("notes")
    )

fun UpdateContactDTO.toCustomerContactUpdatedEvent(customerId: Long, currentContact: ContactDTO): CustomerEvent {
    val changed = mutableSetOf < String >()
    val merged = currentContact.copy(
        name = this.name ?: currentContact.name,
        surname = this.surname ?: currentContact.surname,
        ssn = this.ssn ?: currentContact.ssn,
    ).also {
        if (this.name != null && this.name != currentContact.name) changed += "contact.name"
        if (this.surname != null && this.surname != currentContact.surname) changed += "contact.surname"
        if (this.ssn != null && this.ssn != currentContact.ssn) changed += "contact.ssn"
    }

    return CustomerEvent(
        eventType = CustomerEventType.CONTACT_UPDATED,
        id = customerId,
        contact = merged.toPayload(),
        changedFields = if (changed.isEmpty()) emptySet() else changed.toSet(),
    )
}

fun CustomerDTO.toCustomerDeletedEvent(): CustomerEvent =
    CustomerEvent(
        eventType = CustomerEventType.DELETED,
        id = this.id,
        changedFields = emptySet()
    )