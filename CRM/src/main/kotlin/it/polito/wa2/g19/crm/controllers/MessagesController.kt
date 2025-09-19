package it.polito.wa2.g19.crm.controllers

import it.polito.wa2.g19.crm.dtos.CreateMessageDTO
import it.polito.wa2.g19.crm.dtos.MessageDTO
import it.polito.wa2.g19.crm.dtos.MessageHistoryDTO
import it.polito.wa2.g19.crm.dtos.UpdateMessageDTO
import it.polito.wa2.g19.crm.entities.Channel
import it.polito.wa2.g19.crm.entities.Priority
import it.polito.wa2.g19.crm.entities.State
import it.polito.wa2.g19.crm.exceptions.InvalidDataException
import it.polito.wa2.g19.crm.services.CRMService
import jakarta.validation.Valid
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

private val emailValidator = EmailValidator()
private val phonePattern = Regex("^\\+?[0-9]{10,12}$")

@RestController
@RequestMapping("/API/messages")
class MessagesController(private val crmService: CRMService) {

    @GetMapping("/")
    fun getMessages(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) sender: String?,
        @RequestParam(required = false) subject: String?,
        @RequestParam(required = false) channel: Channel?,
        @RequestParam(required = false) state: State?,
        @RequestParam(required = false) priority: Priority?,
        @RequestParam(required = false) from: LocalDateTime?,
        @RequestParam(required = false) to: LocalDateTime?,
        @RequestParam(defaultValue = "date") sortBy: String,
        @RequestParam(defaultValue = "asc") direction: String
    ): ResponseEntity<Any> {

        val sortDirection = if (direction == "asc") Sort.Direction.ASC else Sort.Direction.DESC
        val pageable: Pageable = try {
            if(sortBy != "sender" && sortBy != "subject" && sortBy != "channel" && sortBy != "state" && sortBy != "priority" && sortBy != "date")
                throw IllegalArgumentException()
            PageRequest.of(page, size, Sort.by(sortDirection, sortBy))
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Invalid paging/sorting parameter"))
        }
        val messages = crmService.getMessages(pageable, sender, subject, channel, state, priority, from, to)

        return ResponseEntity.ok(messages)
    }

    @PostMapping("/")
    fun createMessage(@Valid @RequestBody createMessageDTO: CreateMessageDTO): ResponseEntity<MessageDTO> {
        if (createMessageDTO.channel == Channel.EMAIL && !emailValidator.isValid(createMessageDTO.sender, null))
            throw InvalidDataException("Invalid sender email!")
        if ((createMessageDTO.channel == Channel.PHONE_CALL || createMessageDTO.channel == Channel.TEXT_MESSAGE)
            && !phonePattern.matches(createMessageDTO.sender))
            throw InvalidDataException("Invalid sender phone number!")

        val message = crmService.createMessage(createMessageDTO)
        return ResponseEntity.ok(message)
    }

    @GetMapping("/{id}")
    fun getMessage(@PathVariable id: Long) : ResponseEntity<MessageDTO> {
        val message = crmService.getMessage(id)
        return ResponseEntity.ok(message)
    }

    @PostMapping("/{id}")
    fun updateMessageState(
        @PathVariable id: Long,
        @RequestBody updateMessageDTO: UpdateMessageDTO
    ) : ResponseEntity<String> {
        crmService.updateState(id, updateMessageDTO)
        return ResponseEntity.ok("Message with id $id updated")
    }

    @GetMapping("/{messageId}/history")
    fun getMessageHistory(@PathVariable messageId: Long): ResponseEntity<List<MessageHistoryDTO>> {
        return ResponseEntity.ok(crmService.getMessageHistory(messageId))
    }

    @PutMapping("/{messageId}/priority")
    fun updateMessagePriority(@PathVariable messageId: Long, @RequestBody priority: String): ResponseEntity<Any> {
        val newPriority = try {
            Priority.valueOf(priority)}
        catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Invalid priority value"))
        }
        val updatedMessage = crmService.updateMessagePriority(messageId, newPriority)
        return ResponseEntity.ok(updatedMessage)
    }
}