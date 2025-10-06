package it.polito.wa2.g19.crm.services

import it.polito.wa2.g19.crm.dtos.CreateMessageDTO
import it.polito.wa2.g19.crm.dtos.MessageDTO
import it.polito.wa2.g19.crm.dtos.MessageHistoryDTO
import it.polito.wa2.g19.crm.dtos.UpdateMessageDTO
import it.polito.wa2.g19.crm.entities.Channel
import it.polito.wa2.g19.crm.entities.Priority
import it.polito.wa2.g19.crm.entities.State
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface MessageService {
    fun getMessages(
        pageable: Pageable, sender: String?, subject: String?, channel: Channel?, state: State?,
        priority: Priority?, dateFrom: LocalDateTime?, dateTo: LocalDateTime?): List<MessageDTO>
    fun createMessage(createMessageDTO: CreateMessageDTO): MessageDTO
    fun getMessage(id: Long): MessageDTO
    fun updateState(id: Long, updateMessageDTO: UpdateMessageDTO): MessageDTO
    fun getMessageHistory(messageId: Long): List<MessageHistoryDTO>
    fun updateMessagePriority(messageId: Long, newPriority: Priority): MessageDTO
}