package it.polito.wa2.g19.analytics.repositories

import it.polito.wa2.g19.analytics.models.JobOfferDocument
import org.springframework.data.mongodb.repository.MongoRepository

interface JobOfferRepository : MongoRepository<JobOfferDocument, Long>
