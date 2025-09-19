package it.polito.wa2.g19.crm.controllers

import it.polito.wa2.g19.crm.dtos.CustomerDTO
import it.polito.wa2.g19.crm.services.CRMService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/API/customers")
class CustomersController(private val crmService: CRMService) {
    @PostMapping("/")
    fun createCustomer(@RequestBody customer: CustomerDTO): ResponseEntity<CustomerDTO> {
        val createdCustomer = crmService.createCustomer(customer)
        return ResponseEntity.ok(createdCustomer)
    }

    @GetMapping("/")
    fun getCustomers(): ResponseEntity<List<CustomerDTO>> {
        val customers = crmService.getCustomers()
        return ResponseEntity.ok(customers)
    }

    @GetMapping("/{customerId}")
    fun getCustomer(@PathVariable customerId: Long): ResponseEntity<CustomerDTO> {
        val customer = crmService.getCustomer(customerId)
        return ResponseEntity.ok(customer)
    }

    @PutMapping("/{id}/notes")
    fun updateCustomerNotes(@PathVariable id: Long, @RequestBody notes: List<String>) : ResponseEntity<CustomerDTO> {
        val customerDTO = crmService.updateCustomerNotes(id, notes)
        return ResponseEntity.ok(customerDTO)
    }
}