package it.polito.wa2.g19.crm.repositories

import it.polito.wa2.g19.crm.entities.JobOffer
import org.springframework.data.jpa.repository.JpaRepository

interface JobOfferRepository : JpaRepository<JobOffer, Long> {
    fun findByCustomerId(customerId: Long): List<JobOffer>
    fun findByProfessionalId(professionalId: Long): List<JobOffer>
    fun findByStatus(status: JobOffer.Status): List<JobOffer>
}