package it.polito.wa2.g19.crm.services

import it.polito.wa2.g19.crm.dtos.CreateJobOfferDTO
import it.polito.wa2.g19.crm.dtos.JobOfferDTO
import it.polito.wa2.g19.crm.dtos.JobOfferUpdateDTO
import it.polito.wa2.g19.crm.entities.JobOffer
import org.springframework.data.domain.Pageable

interface JobOfferService {
    fun createJobOffer(createJobOfferDTO: CreateJobOfferDTO): JobOfferDTO
    fun getJobOffers(pageable: Pageable, customerId: Long?, status: JobOffer.Status?, professionalId: Long?): List<JobOfferDTO>
    fun getOpenJobOffers(customerId: Long, pageable: Pageable): List<JobOfferDTO>
    fun getAcceptedJobOffers(professionalId: Long, pageable: Pageable): List<JobOfferDTO>
    fun getAbortedJobOffers(pageable: Pageable, customerId: Long?, professionalId: Long?): List<JobOfferDTO>
    fun updateJobOffer(jobOfferId: Long, requestDTO: JobOfferUpdateDTO): JobOfferDTO
    fun getJobOfferValue(jobOfferId: Long): Float?
}