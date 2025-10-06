package it.polito.wa2.g19.document_store.services

import it.polito.wa2.g19.document_store.dtos.DocumentDTO
import it.polito.wa2.g19.document_store.dtos.DocumentMetadataDTO
import org.springframework.data.domain.Pageable
import org.springframework.web.multipart.MultipartFile

interface DocumentService {
    fun getDocuments(pageable: Pageable): List<DocumentDTO>

    fun getDocumentMetadata(id: Long): DocumentMetadataDTO

    fun getContent(id: Long): DocumentDTO

    fun deleteDocument(id: Long)

    fun uploadDocument(file: MultipartFile): DocumentMetadataDTO?

    fun updateDocument(id: Long, file: MultipartFile): DocumentMetadataDTO?
}
