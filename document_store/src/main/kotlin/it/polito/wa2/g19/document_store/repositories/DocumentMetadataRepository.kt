package it.polito.wa2.g19.document_store.repositories

import it.polito.wa2.g19.document_store.entities.DocumentMetadata
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DocumentMetadataRepository: JpaRepository<DocumentMetadata, Long>{
}