package it.polito.wa2.g19.communication_manager.controllers

import it.polito.wa2.g19.communication_manager.exceptions.InvalidEmailException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class CommunicationManagerExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(InvalidEmailException::class)
    fun handleInvalidEmail(e: InvalidEmailException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message !!)
}