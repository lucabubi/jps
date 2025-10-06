package it.polito.wa2.g19.document_store.controllers

import it.polito.wa2.g19.document_store.dtos.*
import it.polito.wa2.g19.document_store.services.DocumentService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders

@RestController
@RequestMapping("/API/documents")
class DocumentController(private val documentService: DocumentService){
    @GetMapping("/")
    fun getDocuments(pageable: Pageable) : ResponseEntity<List<DocumentDTO>>{
        val documents = documentService.getDocuments(pageable)
        return ResponseEntity.ok(documents)
    }

    @GetMapping("/{id}")
    fun getDocumentMetadata(@PathVariable id: Long) : ResponseEntity<DocumentMetadataDTO>{
        val document = documentService.getDocumentMetadata(id)
        return ResponseEntity.ok(document)
    }

    @GetMapping("/{id}/data")
    fun getContent(@PathVariable id: Long) : ResponseEntity<ByteArrayResource> {
        val documentDTO = documentService.getContent(id)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${documentDTO.meta?.name}\"")
            .header(HttpHeaders.CONTENT_TYPE, documentDTO.meta?.contentType)
            .body(documentDTO.data?.let { ByteArrayResource(it) })
    }

    @DeleteMapping("/{id}")
    fun deleteDocument(@PathVariable id: Long): ResponseEntity<String>{
        documentService.deleteDocument(id)
        return ResponseEntity.ok("Document with id $id deleted")
    }

    @PostMapping("/")
    fun uploadDocument(@RequestParam ("file")file: MultipartFile): ResponseEntity<DocumentMetadataDTO> {
        val metadata = documentService.uploadDocument(file)
        return ResponseEntity.ok(metadata)
    }

    @PutMapping("/{id}")
    fun updateDocument(@PathVariable id: Long, @RequestParam ("file")file: MultipartFile): ResponseEntity<DocumentMetadataDTO> {
        val metadata = documentService.updateDocument(id, file)
        return ResponseEntity.ok(metadata)
    }
}