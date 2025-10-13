package it.polito.wa2.g19.crm.utils

import it.polito.wa2.g19.crm.dtos.ContactDTO
import it.polito.wa2.g19.crm.entities.Category

data class ContactPayload(
    val name: String,
    val surname: String,
    val ssn: String? = null,
    val category: Category,
    val emails: Set<String> = emptySet(),
    val telephones: Set<String> = emptySet(),
    val addresses: Set<String> = emptySet()
)

