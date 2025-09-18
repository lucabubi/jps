package it.polito.wa2.g19.crm2.repositories

import it.polito.wa2.g19.crm2.entities.Email
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailRepository : JpaRepository<Email, Long> {
    fun findByEmailContaining(email: String): List<Email>
}