package it.polito.wa2.g19.crm2.dtos

import it.polito.wa2.g19.crm2.entities.State

class UpdateMessageDTO (
    val state: State,
    val comment: String?
)