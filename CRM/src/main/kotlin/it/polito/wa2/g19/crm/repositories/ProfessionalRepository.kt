package it.polito.wa2.g19.crm.repositories

import it.polito.wa2.g19.crm.entities.Professional
import org.springframework.data.jpa.repository.JpaRepository

interface ProfessionalRepository : JpaRepository<Professional, Long> {
}