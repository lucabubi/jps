package it.polito.wa2.g19.crm.dtos

import java.util.*

data class JobOfferUpdateDTO (
    val status: String,
    val notes: Optional<List<String>> = Optional.empty(),
    val professionalId: Optional<Long> = Optional.empty()){

}