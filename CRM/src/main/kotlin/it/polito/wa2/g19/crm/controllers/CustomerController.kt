package it.polito.wa2.g19.crm.controllers

import it.polito.wa2.g19.crm.dtos.CustomerDTO
import it.polito.wa2.g19.crm.dtos.NoteDTO
import it.polito.wa2.g19.crm.services.CustomerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/API/customers")
class CustomerController(private val customerService: CustomerService) {
    @PostMapping("/")
    fun createCustomer(@RequestBody customer: CustomerDTO): ResponseEntity<CustomerDTO> {
        val createdCustomer = customerService.createCustomer(customer)
        return ResponseEntity.ok(createdCustomer)
    }

    @GetMapping("/")
    fun getCustomers(): ResponseEntity<List<CustomerDTO>> {
        val customers = customerService.getCustomers()
        return ResponseEntity.ok(customers)
    }

    @GetMapping("/{customerId}")
    fun getCustomer(@PathVariable customerId: Long): ResponseEntity<CustomerDTO> {
        val customer = customerService.getCustomer(customerId)
        return ResponseEntity.ok(customer)
    }

    @PutMapping("/{id}/notes")
    fun updateCustomerNotes(@PathVariable id: Long, @RequestBody notes: List<NoteDTO>) : ResponseEntity<CustomerDTO> {
        val customerDTO = customerService.updateCustomerNotes(id, notes)
        return ResponseEntity.ok(customerDTO)
    }

    @DeleteMapping("/{id}")
    fun deleteCustomer(@PathVariable id: Long) : ResponseEntity<Void> {
        customerService.deleteCustomer(id)
        return ResponseEntity.noContent().build()
    }
}