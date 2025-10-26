package it.polito.wa2.g19.crm.services

import it.polito.wa2.g19.crm.dtos.*
import it.polito.wa2.g19.crm.entities.*
import it.polito.wa2.g19.crm.exceptions.*
import it.polito.wa2.g19.crm.repositories.*
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import mu.KotlinLogging

@Service
@Transactional
class CustomerServiceImpl (
    private val customerRepository: CustomerRepository,
) : CustomerService {
    private val logger = KotlinLogging.logger {}

    override fun createCustomer(customerDTO : CustomerDTO): CustomerDTO {
        // Convert DTO to entity
        val customer = Customer(
            contact = customerDTO.contact.toEntity(),
            notes = customerDTO.notes,
            jobOffers = customerDTO.jobOffers.map { it.toEntity() }.toSet()
        )
        logger.info("Creating customer: $customer")
        // Save to database
        val savedCustomer = customerRepository.save(customer)
        logger.info("Customer saved: $savedCustomer")
        // Convert entity back to DTO and return
        return savedCustomer.toDTO()
    }

    override fun getCustomers(): List<CustomerDTO> {
        logger.info("Retrieving all customers")
        // Retrieve customers from database
        val customers = customerRepository.findAll()
        logger.info("Customers retrieved: $customers")
        // Convert entities to DTOs and return
        return customers.map { it.toDTO() }
    }

    override fun getCustomer(customerId: Long): CustomerDTO {
        logger.info("Retrieving customer with id: $customerId")
        // Retrieve customer from database
        val customer = customerRepository.findById(customerId)
            .orElseThrow { CustomerNotFoundException("Customer with id $customerId not found") }
        logger.info("Customer retrieved: $customer")
        // Convert entity to DTO and return
        return customer.toDTO()
    }

    override fun updateCustomerNotes(id: Long, notes: List<String>) : CustomerDTO {
        val customer = customerRepository.findById(id).orElseThrow { CustomerNotFoundException("Contact with id $id not found") }
        logger.info { "Updating customer id:$id notes..." }
        customer.notes = notes
        // .save added for good practice, even if not needed because of "dirty checking" performed by Spring Data JPA
        customerRepository.save(customer)
        logger.info { "Customer id:$id notes updated" }
        return customer.toDTO()
    }





}