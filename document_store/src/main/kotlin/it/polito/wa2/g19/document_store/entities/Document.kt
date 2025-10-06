package it.polito.wa2.g19.document_store.entities

import jakarta.persistence.*



@Entity
class Document (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long = 0,
  var data: ByteArray? = null,
     @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "meta_id", referencedColumnName = "id")
        var meta: DocumentMetadata? = null
){
    fun documentEquals(doc2: Document): Boolean {
        return this.data.contentEquals(doc2.data) && this.meta?.name.contentEquals(doc2.meta?.name)
    }

}
