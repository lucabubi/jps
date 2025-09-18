package it.polito.wa2.g19.crm2.dtos

import jakarta.validation.constraints.Size

data class UpdateContactDTO(
    val name: String? = null,
    val surname: String? = null,
    @field:Size(min = 5, max = 20, message = "SSN must be 5-20 characters long!")
    val ssn: String? = null,
)