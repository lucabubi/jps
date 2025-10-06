package it.polito.wa2.g19.document_store.services

import it.polito.wa2.g19.document_store.dtos.DocumentDTO
import it.polito.wa2.g19.document_store.dtos.DocumentMetadataDTO
import it.polito.wa2.g19.document_store.dtos.toDTO
import it.polito.wa2.g19.document_store.dtos.toEntity
import it.polito.wa2.g19.document_store.entities.Document
import it.polito.wa2.g19.document_store.exceptions.DocumentNotFoundException
import it.polito.wa2.g19.document_store.exceptions.DuplicateDocumentException
import it.polito.wa2.g19.document_store.repositories.DocumentRepository
import it.polito.wa2.g19.document_store.repositories.DocumentMetadataRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import mu.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime


@Service
@Transactional
class DocumentServiceImpl(private val documentRepository : DocumentRepository,
                          private val documentMetadataRepository: DocumentMetadataRepository):
    DocumentService {

    private val logger = KotlinLogging.logger {}
    override fun getDocuments(pageable: Pageable): List<DocumentDTO> {
        return documentRepository.findAll(pageable).content.map{ it.toDTO() }
    }

    override fun getDocumentMetadata(id: Long): DocumentMetadataDTO {
        val existingDocument = documentMetadataRepository.findById(id).orElseThrow {DocumentNotFoundException("Metadata with id: $id not found")}
        return existingDocument.toDTO()
    }

    override fun getContent(id: Long): DocumentDTO{
        val existingDocument = documentRepository.findById(id).orElseThrow {DocumentNotFoundException("Document with id: $id not found")}
        return existingDocument.toDTO()
    }

    override fun deleteDocument(id: Long ) {
        logger.info { "Deleting document with id $id" }
        documentRepository.findById(id).orElseThrow {DocumentNotFoundException("Document with id: $id not found")}
        documentMetadataRepository.deleteById(id)
        documentRepository.deleteById(id)
    }

    override fun uploadDocument(file: MultipartFile): DocumentMetadataDTO? {
        val existingDocuments: List<Document> = documentRepository.findAll()
        val metadataDTO = DocumentMetadataDTO(
            name = file.originalFilename ?: "",
            size = file.size,
            contentType = file.contentType,
            uploadDate = LocalDateTime.now()
        )
        val documentDTO = DocumentDTO(data = file.bytes, meta = metadataDTO.toEntity())
        val document = documentDTO.toEntity()
        for (existingDocument in existingDocuments)
            if (existingDocument.documentEquals(document))
                throw DuplicateDocumentException("Document already exists")
        logger.info { "Creating file ${file.originalFilename}..." }
        documentRepository.save(document)
        logger.info { "File ${file.originalFilename} created" }
        return document.meta?.toDTO()
    }

    override fun updateDocument(id: Long, file: MultipartFile): DocumentMetadataDTO? {
        val existingDocument = documentRepository.findById(id).orElseThrow { DocumentNotFoundException("Document with id: $id not found")}
        logger.info { "Updating file ${file.originalFilename}..." }
        val metadataDTO = DocumentMetadataDTO(
            name = file.originalFilename ?: "",
            size = file.size,
            contentType = file.contentType,
            uploadDate = LocalDateTime.now()
        )
        val documentDTO = DocumentDTO(data = file.bytes, meta = existingDocument.meta)
        if(existingDocument.toDTO().documentEquals(documentDTO))
            throw DuplicateDocumentException("Document already exists")
        existingDocument.meta?.apply{
            name = metadataDTO.name
            contentType = metadataDTO.contentType
            size = metadataDTO.size
        }
        existingDocument.data.apply {
            documentDTO.data
        }
        val updatedDocument = documentRepository.save(existingDocument)
        logger.info { "File ${file.originalFilename} updated" }
        return updatedDocument.meta?.toDTO()
    }
}