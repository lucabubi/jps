package it.polito.wa2.g19.document_store.dtos

import it.polito.wa2.g19.document_store.entities.DocumentMetadata
import java.time.LocalDateTime

data class DocumentMetadataDTO(
    val id: Long = 0,
    val name: String = "",
    val size: Long = 0,
    val contentType: String? = null,
    val uploadDate: LocalDateTime)

fun DocumentMetadata.toDTO() = DocumentMetadataDTO(
    id = this.id,
    name = this.name,
    size = this.size,
    contentType = this.contentType,
    uploadDate = this.uploadDate
)

fun DocumentMetadataDTO.toEntity() = DocumentMetadata(
    name = this.name,
    size = this.size,
    contentType = this.contentType,
    uploadDate = this.uploadDate
)


