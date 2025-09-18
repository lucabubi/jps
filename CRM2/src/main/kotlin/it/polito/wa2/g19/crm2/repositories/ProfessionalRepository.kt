package it.polito.wa2.g19.crm2.repositories

import it.polito.wa2.g19.crm2.entities.Professional
import org.springframework.data.jpa.repository.JpaRepository

interface ProfessionalRepository : JpaRepository<Professional, Long> {
}