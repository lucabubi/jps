package it.polito.wa2.g19.communication_manager.services

import it.polito.wa2.g19.communication_manager.dtos.SendEmailDTO

interface CMService {
    fun sendEmail(sendEmailDTO: SendEmailDTO): SendEmailDTO
}