package it.polito.wa2.g19.crm.repositories

import it.polito.wa2.g19.crm.entities.Note
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoteRepository : JpaRepository<Note, Long>