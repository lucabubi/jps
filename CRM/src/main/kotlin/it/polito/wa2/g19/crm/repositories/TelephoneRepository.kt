package it.polito.wa2.g19.crm.repositories

import it.polito.wa2.g19.crm.entities.Telephone
import org.springframework.data.jpa.repository.JpaRepository


interface TelephoneRepository : JpaRepository<Telephone, Long> {
    fun findByTelephoneContaining(number: String): List<Telephone>
}