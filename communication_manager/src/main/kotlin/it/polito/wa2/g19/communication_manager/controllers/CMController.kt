package it.polito.wa2.g19.communication_manager.controllers

import it.polito.wa2.g19.communication_manager.dtos.SendEmailDTO
import it.polito.wa2.g19.communication_manager.exceptions.*
import it.polito.wa2.g19.communication_manager.services.CMService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import java.security.Principal

@RestController
@RequestMapping("/API/emails")
class CMController(private val cmService: CMService) {
    private val emailValidator = EmailValidator()

    @PostMapping("/")
    fun sendEmail(@RequestBody sendEmailDTO: SendEmailDTO): ResponseEntity<SendEmailDTO> {
        if(!emailValidator.isValid(sendEmailDTO.recipient, null))
            throw InvalidEmailException("Invalid email address")
        val response = cmService.sendEmail(sendEmailDTO)
        return ResponseEntity.ok(response)
    }
}