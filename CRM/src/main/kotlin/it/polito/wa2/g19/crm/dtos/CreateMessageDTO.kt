package it.polito.wa2.g19.crm.dtos

import it.polito.wa2.g19.crm.entities.*

data class CreateMessageDTO (
    val sender: String,
    val subject: String?,
    val body: String?,
    val channel: Channel,
    val priority: Priority?,
    val history: List<MessageHistoryDTO> = emptyList()
){

}