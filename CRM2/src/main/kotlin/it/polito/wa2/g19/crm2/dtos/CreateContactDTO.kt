package it.polito.wa2.g19.crm2.dtos

import it.polito.wa2.g19.crm2.entities.Category
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateContactDTO (
    @field:NotBlank(message = "Name cannot be blank!")
    val name: String,
    @field:NotBlank(message = "Surname cannot be blank!")
    val surname: String,
    @field:Size(min = 5, max = 20, message = "SSN must be 5-20 characters long!")
    val ssn: String? = null,
    val category: Category = Category.UNKNOWN,
    val emails: List<String> = emptyList(),
    val telephones: List<String> = emptyList(),
    val addresses: List<String> = emptyList()
){

}