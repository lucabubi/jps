package it.polito.wa2.g19.crm.repositories

import it.polito.wa2.g19.crm.entities.Email
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailRepository : JpaRepository<Email, Long> {
    fun findByEmailContaining(email: String): List<Email>
}