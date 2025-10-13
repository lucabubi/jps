package it.polito.wa2.g19.analytics.repositories

import it.polito.wa2.g19.analytics.models.CustomerDocument
import org.springframework.data.mongodb.repository.MongoRepository

interface CustomerRepository : MongoRepository<CustomerDocument, Long>
