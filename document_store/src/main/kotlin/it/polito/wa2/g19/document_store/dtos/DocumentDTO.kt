package it.polito.wa2.g19.document_store.dtos

import it.polito.wa2.g19.document_store.entities.Document
import it.polito.wa2.g19.document_store.entities.DocumentMetadata

data class DocumentDTO(
    val id: Long = 0,
    val data: ByteArray? = null,
    val meta: DocumentMetadata? = null){
    fun documentEquals(doc2: DocumentDTO): Boolean {
    return this.data.contentEquals(doc2.data) && this.meta?.name.contentEquals(doc2.meta?.name)
    }
}

fun Document.toDTO() = DocumentDTO(
    id = this.id,
    data = this.data,
    meta = this.meta
)

fun DocumentDTO.toEntity() = Document(
    data = this.data,
    meta = this.meta
)