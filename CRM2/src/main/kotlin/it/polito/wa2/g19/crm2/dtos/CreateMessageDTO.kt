package it.polito.wa2.g19.crm2.dtos

import it.polito.wa2.g19.crm2.entities.*

data class CreateMessageDTO (
    val sender: String,
    val subject: String?,
    val body: String?,
    val channel: Channel,
    val priority: Priority?,
    val history: List<MessageHistoryDTO> = emptyList()
){

}