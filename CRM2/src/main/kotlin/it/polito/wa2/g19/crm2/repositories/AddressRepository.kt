package it.polito.wa2.g19.crm2.repositories

import it.polito.wa2.g19.crm2.entities.Address
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository : JpaRepository<Address, Long> {
}