package it.polito.wa2.g19.document_store.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class DocumentMetadata (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: String = "",
    var size: Long = 0,
    var contentType: String? = null,
    var uploadDate: LocalDateTime = LocalDateTime.now()
){

}