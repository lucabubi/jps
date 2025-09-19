package it.polito.wa2.g19.crm.repositories

import it.polito.wa2.g19.crm.entities.Contact
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactRepository : JpaRepository<Contact, Long>{
    fun findByNameContaining(firstName: String, pageable: Pageable): Page<Contact>
    fun findBySurnameContaining(lastName: String, pageable: Pageable): Page<Contact>
}

