package it.polito.wa2.g19.crm.services

import it.polito.wa2.g19.crm.dtos.ProfessionalDTO
import it.polito.wa2.g19.crm.dtos.ProfessionalUpdateDTO
import it.polito.wa2.g19.crm.entities.JobOffer
import it.polito.wa2.g19.crm.entities.Professional
import it.polito.wa2.g19.crm.events.toProfessionalCreatedEvent
import it.polito.wa2.g19.crm.events.toProfileUpdatedEvent
import it.polito.wa2.g19.crm.exceptions.ProfessionalNotAvailableException
import it.polito.wa2.g19.crm.exceptions.ProfessionalNotFoundException
import it.polito.wa2.g19.crm.messaging.ProfessionalEventsProducer
import jakarta.transaction.Transactional
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*
import it.polito.wa2.g19.crm.repositories.ProfessionalRepository
import mu.KotlinLogging

@Service
@Transactional
class ProfessionalServiceImpl(
    private val professionalRepository: ProfessionalRepository,
    private val professionalEventsProducer: ProfessionalEventsProducer
) : ProfessionalService {
    private val logger = KotlinLogging.logger {}

    override fun createProfessional(professionalDTO: ProfessionalDTO): ProfessionalDTO {
        // Convert DTO to entity
        val professional = Professional(
            contact = professionalDTO.contact.toEntity(),
            notes = professionalDTO.notes,
            skills = professionalDTO.skills,
            dailyRate = professionalDTO.dailyRate,
            employmentState = professionalDTO.employmentState,
            location = professionalDTO.location
        )
        logger.info("Creating professional: $professional")
        // Save to database
        val savedProfessional = professionalRepository.save(professional)
        logger.info("Professional saved: $savedProfessional")
        val savedProfessionalDTO = savedProfessional.toDTO()
        // Publish event
        professionalEventsProducer.publish(savedProfessionalDTO.toProfessionalCreatedEvent())
        // Return
        return savedProfessionalDTO
    }

    override fun getProfessionals(
        pageable: Pageable,
        employmentState: Optional<Professional.State>,
        location: Optional<String>,
        skills: Optional<List<String>>
    ) : List<ProfessionalDTO> {
        logger.info { "Retrieving professionals..." }
        var professionals = professionalRepository.findAll(pageable).toList()
        logger.info { "Professionals retrieved!" }
        logger.info { "Applying filters..." }
        if (employmentState.isPresent) {
            professionals = professionals.filter { it.employmentState == employmentState.get() }
        }
        if (location.isPresent) {
            professionals = professionals.filter { it.location == location.get() }
        }
        if (skills.isPresent) {
            professionals = professionals.filter { it.skills.containsAll(skills.get()) }
        }
        logger.info { "Filters applied!" }
        return professionals.map { it.toDTO() }
    }

    override fun getProfessional(professionalId: Long): ProfessionalDTO {
        logger.info("Retrieving professional with id: $professionalId")
        // Retrieve professional from database
        val professional = professionalRepository.findById(professionalId)
            .orElseThrow { ProfessionalNotFoundException("Professional with id $professionalId not found") }
        logger.info("Professional retrieved: $professional")
        // Convert entity to DTO and return
        return professional.toDTO()
    }

    override fun updateProfessional(id: Long, updateDTO: ProfessionalUpdateDTO) : ProfessionalDTO {
        val professional = professionalRepository.findById(id)
            .orElseThrow { ProfessionalNotFoundException("Professional with id $id not found") }
        val activeJobOffer = professional.jobOffers.filter { it.status == JobOffer.Status.CONSOLIDATED }
        if(activeJobOffer.isNotEmpty() && updateDTO.employmentState.isPresent)
            throw ProfessionalNotAvailableException("Professional with id $id is currently working")
        val current = professional.toDTO()
        logger.info { "Updating professional id:$id..." }
        updateDTO.notes.ifPresent { professional.notes = it }
        updateDTO.skills.ifPresent { professional.skills = it }
        updateDTO.dailyRate.ifPresent { professional.dailyRate = it }
        updateDTO.employmentState.ifPresent { professional.employmentState = it }
        updateDTO.location.ifPresent { professional.location = it }
        // .save added for good practice, even if not needed because of "dirty checking" performed by Spring Data JPA
        professionalRepository.save(professional)
        logger.info { "Professional id:$id updated" }
        // publish event
        professionalEventsProducer.publish(updateDTO.toProfileUpdatedEvent(id, current))
        return professional.toDTO()
    }
}