package it.polito.wa2.g19.crm.repositories

import it.polito.wa2.g19.crm.entities.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository : JpaRepository<Customer, Long> {
}