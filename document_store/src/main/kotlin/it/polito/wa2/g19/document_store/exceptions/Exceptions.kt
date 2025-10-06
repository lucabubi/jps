package it.polito.wa2.g19.document_store.exceptions

class DocumentNotFoundException(message: String) : RuntimeException(message)

class DuplicateDocumentException(message: String) : RuntimeException(message)