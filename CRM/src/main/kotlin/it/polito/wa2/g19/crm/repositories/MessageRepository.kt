package it.polito.wa2.g19.crm.repositories

import it.polito.wa2.g19.crm.entities.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<Message, Long> {
}