package it.polito.wa2.g19.crm.services

import it.polito.wa2.g19.crm.dtos.CreateJobOfferDTO
import it.polito.wa2.g19.crm.dtos.CustomerMinimalDTO
import it.polito.wa2.g19.crm.dtos.JobOfferDTO
import it.polito.wa2.g19.crm.dtos.JobOfferUpdateDTO
import it.polito.wa2.g19.crm.entities.JobOffer
import it.polito.wa2.g19.crm.entities.Professional
import it.polito.wa2.g19.crm.exceptions.*
import it.polito.wa2.g19.crm.repositories.*
import jakarta.transaction.Transactional
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import mu.KotlinLogging

@Service
@Transactional
class JobOfferServiceImpl(
    private val jobOfferRepository: JobOfferRepository,
    private val customerRepository: CustomerRepository,
    private val professionalRepository: ProfessionalRepository,
) : JobOfferService {
    private val logger = KotlinLogging.logger {}

    override fun createJobOffer(createJobOfferDTO: CreateJobOfferDTO): JobOfferDTO {
        val customer = customerRepository.findById(createJobOfferDTO.customerId)
            .orElseThrow { CustomerNotFoundException("Customer with id ${createJobOfferDTO.customerId} not found") }

        val jobOffer = JobOfferDTO(
            description = createJobOfferDTO.description,
            title = createJobOfferDTO.title,
            duration = createJobOfferDTO.duration,
            notes = createJobOfferDTO.notes,
            requiredSkills = createJobOfferDTO.requiredSkills,
            customer = CustomerMinimalDTO(
                id = customer.id,
                contact = customer.contact.toDTO(),
                notes = customer.notes
            )
        ).toEntity()
        logger.info("Creating job offer: $jobOffer")
        val savedJobOffer = jobOfferRepository.save(jobOffer)
        logger.info("Job offer saved: $savedJobOffer ${savedJobOffer.customer.contact.id}")
        return savedJobOffer.toDTO()
    }


    override fun getJobOffers(pageable: Pageable, customerId: Long?, status: JobOffer.Status?, professionalId: Long?): List<JobOfferDTO> {
        var jobOffers = jobOfferRepository.findAll(pageable).toList()
        if (customerId != null) {
            jobOffers = jobOffers.filter { it.customer.id == customerId }
        }
        if (status != null) {
            jobOffers = jobOffers.filter { it.status == status }
        }
        if(professionalId != null){
            jobOffers = jobOffers.filter { it.professional?.id == professionalId }
        }
        return jobOffers.take(pageable.pageSize).map{ it.toDTO() }
    }

    override fun getOpenJobOffers(customerId: Long, pageable: Pageable): List<JobOfferDTO> {
        val customer = customerRepository.findById(customerId).orElseThrow { CustomerNotFoundException("Customer with id $customerId not found") }
        val jobOffers = customer.jobOffers.filter { !((it.status == JobOffer.Status.ABORTED)
                || (it.status == JobOffer.Status.CONSOLIDATED)
                || (it.status == JobOffer.Status.DONE)) }
        return jobOffers.take(pageable.pageSize).map{ it.toDTO() }

    }

    override fun getAcceptedJobOffers(professionalId: Long, pageable: Pageable): List<JobOfferDTO> {
        val professional = professionalRepository.findById(professionalId).orElseThrow { ProfessionalNotFoundException("Professional with id $professionalId not found") }
        val jobOffers = professional.jobOffers.filter { it.status == JobOffer.Status.CONSOLIDATED || it.status == JobOffer.Status.DONE }
        return jobOffers.take(pageable.pageSize).map { it.toDTO() }
    }

    override fun getAbortedJobOffers(pageable: Pageable, customerId: Long?, professionalId: Long?): List<JobOfferDTO> {
        var jobOffers = jobOfferRepository.findAll().filter { it.status == JobOffer.Status.ABORTED }
        if (customerId != null) {
            val customer = customerRepository.findById(customerId).orElseThrow { CustomerNotFoundException("Customer with id $customerId not found") }
            jobOffers = jobOffers.filter { it.customer == customer }
        }
        if (professionalId != null) {
            val professional = professionalRepository.findById(professionalId).orElseThrow { ProfessionalNotFoundException("Professional with id $professionalId not found") }
            jobOffers = jobOffers.filter { it.professional == professional }
        }
        return jobOffers.take(pageable.pageSize).map { it.toDTO() }
    }

    override fun updateJobOffer(jobOfferId: Long, requestDTO: JobOfferUpdateDTO): JobOfferDTO {
        val jobOffer = jobOfferRepository.findById(jobOfferId).orElseThrow { JobOfferNotFoundException("Job offer with id $jobOfferId not found") }
        logger.info("Updating job offer with id $jobOfferId")
        when (requestDTO.status) {
            "SELECTION_PHASE" -> {
                if(jobOffer.status == JobOffer.Status.ABORTED)
                    throw InvalidStatusException("Job offer status can't be ABORTED to apply change")
                if (requestDTO.professionalId.isPresent)
                    throw InvalidStatusException("Id of professional is not required for status SELECTION_PHASE")
                jobOffer.status = JobOffer.Status.SELECTION_PHASE
            }
            "CANDIDATE_PROPOSAL" -> {
                if(jobOffer.status != JobOffer.Status.SELECTION_PHASE)
                    throw InvalidStatusException("Job offer status must be SELECTION_PHASE to apply change")
                if (requestDTO.professionalId.isPresent)
                    throw InvalidStatusException("Id of professional is not required for status CANDIDATE_PROPOSAL")
                jobOffer.status = JobOffer.Status.CANDIDATE_PROPOSAL
            }
            "CONSOLIDATED" -> {
                if(jobOffer.status != JobOffer.Status.CANDIDATE_PROPOSAL)
                    throw InvalidStatusException("Job offer status must be CANDIDATE_PROPOSAL to apply change")
                if (!requestDTO.professionalId.isPresent)
                    throw InvalidStatusException("Id of professional is required for status CONSOLIDATED")
                val professional = professionalRepository.findById(requestDTO.professionalId.get())
                    .orElseThrow { ProfessionalNotFoundException("Professional not found") }
                if(professional.employmentState != Professional.State.AVAILABLE_FOR_WORK)
                    throw ProfessionalNotAvailableException("Professional with id ${professional.id} is currently working")
                professional.employmentState = Professional.State.EMPLOYED
                jobOffer.status = JobOffer.Status.CONSOLIDATED
                jobOffer.professional = professional
                jobOffer.calculateValue()
            }
            "DONE" -> {
                if(jobOffer.status != JobOffer.Status.CONSOLIDATED)
                    throw InvalidStatusException("Job offer status must be CONSOLIDATE to apply change")
                if (requestDTO.professionalId.isPresent)
                    throw InvalidStatusException("Id of professional is not required for status DONE")
                jobOffer.professional!!.employmentState = Professional.State.AVAILABLE_FOR_WORK
                jobOffer.status = JobOffer.Status.DONE
            }
            "ABORTED" -> {
                if(jobOffer.status == JobOffer.Status.DONE)
                    throw InvalidStatusException("Job offer status can't be DONE to apply change")
                if (requestDTO.professionalId.isPresent)
                    throw InvalidStatusException("Id of professional is not required for status ABORTED")
                jobOffer.professional!!.employmentState = Professional.State.AVAILABLE_FOR_WORK
                jobOffer.status = JobOffer.Status.ABORTED
            }
            else -> {
                throw InvalidStatusException("Invalid status")
            }
        }

        requestDTO.notes.ifPresent {
            jobOffer.notes = requestDTO.notes.get()
        }
        val changedFields = mutableSetOf<String>()
        changedFields.add("status")
        if(requestDTO.notes.isPresent){
            changedFields.add("notes")
        }
        return jobOffer.toDTO()
    }

    override fun getJobOfferValue(jobOfferId: Long): Float? {
        val jobOffer = jobOfferRepository.findById(jobOfferId).orElseThrow { JobOfferNotFoundException("Job offer with id $jobOfferId not found") }
        if(jobOffer.professional == null){
            throw ProfessionalNotAvailableException("No professional assigned")
        }
        return jobOffer.value
    }
}