package it.polito.wa2.g19.crm.dtos

import it.polito.wa2.g19.crm.entities.Note
import java.time.LocalDateTime


data class NoteDTO(
    val id : Long = 0,
    val title : String,
    val createdAt : LocalDateTime,
    val description : String?,
){
    fun toEntity() = Note(
        id = this.id,
        title = this.title,
        createdAt = this.createdAt,
        description = this.description,
    )
}