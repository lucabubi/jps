package it.polito.wa2.g19.crm2.repositories

import it.polito.wa2.g19.crm2.entities.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository : JpaRepository<Customer, Long> {
}