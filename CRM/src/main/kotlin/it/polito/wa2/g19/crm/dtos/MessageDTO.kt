package it.polito.wa2.g19.crm.dtos

import it.polito.wa2.g19.crm.entities.*
import java.time.LocalDateTime

data class MessageDTO(
    val id: Long,
    val sender: String,
    var date: LocalDateTime,
    val subject: String?,
    val body: String?,
    val channel: Channel,
    val state: State,
    val priority: Priority,
    val history: List<MessageHistoryDTO>
)

data class MessageHistoryDTO(
    val id: Long,
    val date: LocalDateTime,
    val state: State,
    val comment: String?,
    val messageId: Long
)

fun Message.toDTO() = MessageDTO(
    id = this.id,
    sender = this.sender,
    date = this.date,
    subject = this.subject,
    body = this.body,
    channel = this.channel,
    state = this.getState(),
    priority = this.priority,
    history = this.history.map { it.toDTO() }
)

fun History.toDTO() = MessageHistoryDTO(
    id = this.id,
    date = this.date,
    state = this.state,
    comment = this.comment,
    messageId = this.message.id
)

fun MessageHistoryDTO.toEntity(message: Message) = History(
    id = this.id,
    date = this.date,
    state = this.state,
    comment = this.comment,
    message = message
)