package it.polito.wa2.g19.crm.controllers

import it.polito.wa2.g19.crm.exceptions.*
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class CRMExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(CustomerNotFoundException::class)
    fun handleCustomerNotFound(e: CustomerNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message !!)

    @ExceptionHandler(ProfessionalNotFoundException::class)
    fun handleProfessionalNotFound(e: ProfessionalNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message !!)

    @ExceptionHandler(JobOfferNotFoundException::class)
    fun handleJobOfferNotFoundException(e: JobOfferNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message !!)

    @ExceptionHandler(ProfessionalNotAvailableException::class)
    fun handleProfessionalNotAvailableException(e: ProfessionalNotAvailableException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message !!)

    @ExceptionHandler(InvalidStatusException::class)
    fun handleInvalidStatusException(e: InvalidStatusException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message !!)

    @ExceptionHandler(MessageNotFoundException::class)
    fun handleMessageNotFound(e: MessageNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message !!)

    @ExceptionHandler(ContactNotFoundException::class)
    fun handleContactNotFound(e: ContactNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message !!)

    @ExceptionHandler(DuplicatedDataException::class)
    fun handleDuplicatedData(e: DuplicatedDataException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)

    @ExceptionHandler(InvalidDataException::class)
    fun handleInvalidData(e: InvalidDataException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)

    @ExceptionHandler(EmailNotFoundException::class)
    fun handleEmailNotFound(e: EmailNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(TelephoneNotFoundException::class)
    fun handleTelephoneNotFound(e: TelephoneNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(AddressNotFoundException::class)
    fun handleAddressNotFound(e: AddressNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)
}