package it.polito.wa2.g19.crm.services

import it.polito.wa2.g19.crm.dtos.*

interface CustomerService {
    fun createCustomer(customerDTO: CustomerDTO): CustomerDTO
    fun getCustomers(): List<CustomerDTO>
    fun getCustomer(customerId: Long): CustomerDTO
    fun deleteCustomer(customerId: Long)
    fun updateCustomerNotes(id: Long, notes: List<String>) : CustomerDTO


}