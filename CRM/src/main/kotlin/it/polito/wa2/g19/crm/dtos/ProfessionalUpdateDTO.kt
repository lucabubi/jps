package it.polito.wa2.g19.crm.dtos

import it.polito.wa2.g19.crm.entities.Professional.State
import java.util.*

data class ProfessionalUpdateDTO(
    val notes: Optional<List<String>> = Optional.empty(),
    val skills: Optional<Set<String>> = Optional.empty(),
    val dailyRate: Optional<Float> = Optional.empty(),
    val employmentState: Optional<State> = Optional.empty(),
    val location: Optional<String> = Optional.empty()
){

}
