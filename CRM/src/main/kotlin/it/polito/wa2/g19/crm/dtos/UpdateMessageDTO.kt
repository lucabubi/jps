package it.polito.wa2.g19.crm.dtos

import it.polito.wa2.g19.crm.entities.State

class UpdateMessageDTO (
    val state: State,
    val comment: String?
)