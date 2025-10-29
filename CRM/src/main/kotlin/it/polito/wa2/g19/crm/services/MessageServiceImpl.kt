package it.polito.wa2.g19.crm.services

import it.polito.wa2.g19.crm.dtos.*
import it.polito.wa2.g19.crm.entities.*
import it.polito.wa2.g19.crm.repositories.*
import it.polito.wa2.g19.crm.exceptions.InvalidDataException
import it.polito.wa2.g19.crm.exceptions.MessageNotFoundException
import jakarta.transaction.Transactional
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import mu.KotlinLogging

@Service
@Transactional
class MessageServiceImpl(
    private val messageRepository: MessageRepository,
    private val emailRepository: EmailRepository,
    private val telephoneRepository: TelephoneRepository,
    private val contactService: ContactService
) : MessageService {
    private val logger = KotlinLogging.logger {}

    override fun getMessages(
        pageable: Pageable,
        sender: String?, subject: String?, channel: Channel?, state: State?,
        priority: Priority?, dateFrom: LocalDateTime?, dateTo: LocalDateTime?): List<MessageDTO> {
        var messages = messageRepository.findAll(pageable.sort).toMutableList()
        if(sender != null){
            messages = messages.filter { it.sender == sender }.toMutableList()
        }
        if(subject != null){
            messages = messages.filter { it.subject?.contains(subject, ignoreCase = true) == true }.toMutableList()
        }
        if(channel != null){
            messages = messages.filter { it.channel == channel }.toMutableList()
        }
        if(state != null){
            messages = messages.filter { it.getState() == state }.toMutableList()
        }
        if(priority != null){
            messages = messages.filter { it.priority == priority }.toMutableList()
        }
        if(dateFrom != null){
            messages = messages.filter { it.date >= dateFrom }.toMutableList()
        }
        if(dateTo != null){
            messages = messages.filter { it.date <= dateTo }.toMutableList()
        }
        return messages.take(pageable.pageSize).map(Message::toDTO)
    }

    override fun createMessage(createMessageDTO: CreateMessageDTO): MessageDTO {

        val message = Message(
            sender = createMessageDTO.sender,
            date = LocalDateTime.now(),
            subject = createMessageDTO.subject,
            body = createMessageDTO.body,
            channel = createMessageDTO.channel,
            state = State.RECEIVED,
            priority = createMessageDTO.priority ?: Priority.LOW
        )
        if(message.channel == Channel.EMAIL){
            val email = emailRepository.findByEmailContaining(message.sender).firstOrNull()
            if(email == null) {
                val newContact = CreateContactDTO("unknown", "unknown", region= Region.UNKNOWN, emails = listOf(message.sender))
                contactService.createContact(newContact)
            }
        }else{
            val telephone = telephoneRepository.findByTelephoneContaining(message.sender).firstOrNull()
            if(telephone == null) {
                val newContact = CreateContactDTO("unknown", "unknown", region= Region.UNKNOWN, telephones = listOf(message.sender))
                contactService.createContact(newContact)
            }
        }
        messageRepository.save(message)
        return message.toDTO()
    }

    override fun getMessage(id: Long): MessageDTO {
        val existingMessage = messageRepository.findById(id).orElseThrow { MessageNotFoundException("Message with id: $id not found") }
        return existingMessage.toDTO()
    }

    @Transactional
    override fun updateState(id: Long, updateMessageDTO: UpdateMessageDTO): MessageDTO {
        val existingMessage = messageRepository.findById(id).orElseThrow { MessageNotFoundException("Message with id: $id not found") }
        if (existingMessage.getState() == State.DISCARDED || existingMessage.getState() == State.FAILED || existingMessage.getState() == State.DONE)
            throw InvalidDataException("Message already done or discarded or failed")
        if(updateMessageDTO.state == State.RECEIVED)
            throw InvalidDataException("State cannot be set to RECEIVED")
        if(updateMessageDTO.state == State.PROCESSING && existingMessage.getState() != State.READ)
            throw InvalidDataException("State cannot be set to PROCESSING")
        if(updateMessageDTO.state == State.READ && existingMessage.getState() != State.RECEIVED)
            throw InvalidDataException("State cannot be set to READ")
        if(updateMessageDTO.state == State.DISCARDED && existingMessage.getState() != State.READ)
            throw InvalidDataException("State cannot be set to DISCARDED")
        if(updateMessageDTO.state == State.FAILED && (existingMessage.getState() != State.PROCESSING || existingMessage.getState() != State.READ))
            throw InvalidDataException("State cannot be set to FAILED")
        val history = History(date = LocalDateTime.now(), state = updateMessageDTO.state, comment = updateMessageDTO.comment, message = existingMessage)
        existingMessage.setState(updateMessageDTO.state)
        existingMessage.history.add(history)
        messageRepository.save(existingMessage)
        return existingMessage.toDTO()
    }

    override fun getMessageHistory(messageId: Long): List<MessageHistoryDTO> {
        val message =
            messageRepository.findById(messageId).orElseThrow { MessageNotFoundException("Message with id: $messageId not found") }
        val messageDTO = message.toDTO()
        return messageDTO.history
    }

    override fun updateMessagePriority(messageId: Long, newPriority: Priority): MessageDTO {
        val message = messageRepository.findById(messageId).orElseThrow { MessageNotFoundException("Message with id: $messageId not found") }
        logger.info("Updating message with id $messageId")
        message.priority = newPriority
        val updatedMessage = messageRepository.save(message)
        logger.info ("Message with id $messageId updated")
        val updatedMessageDTO = updatedMessage.toDTO()
        return updatedMessageDTO
    }
}