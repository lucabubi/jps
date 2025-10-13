package it.polito.wa2.g19.analytics.repositories

import it.polito.wa2.g19.analytics.models.ProfessionalDocument
import org.springframework.data.mongodb.repository.MongoRepository

interface ProfessionalRepository : MongoRepository<ProfessionalDocument, Long>
