package it.polito.wa2.g19.communication_manager.dtos

data class SendEmailDTO(
    val recipient: String,
    val subject: String,
    val body: String
){
}
