package it.polito.wa2.g19.crm.services

import it.polito.wa2.g19.crm.dtos.ProfessionalDTO
import it.polito.wa2.g19.crm.dtos.ProfessionalUpdateDTO
import it.polito.wa2.g19.crm.entities.Professional
import org.springframework.data.domain.Pageable
import java.util.*

interface ProfessionalService {
    fun createProfessional(professionalDTO: ProfessionalDTO): ProfessionalDTO
    fun getProfessionals(
        pageable: Pageable,
        employmentState: Optional<Professional.State>,
        location: Optional<String>,
        skills: Optional<List<String>>
    ) : List<ProfessionalDTO>
    fun getProfessional(professionalId: Long): ProfessionalDTO
    fun updateProfessional(id: Long, updateDTO: ProfessionalUpdateDTO) : ProfessionalDTO
    fun deleteProfessional(id: Long)
}