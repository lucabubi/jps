package it.polito.wa2.g19.crm.dtos

data class CreateJobOfferDTO(
    val description: String = "",
    val title: String = "",
    val duration: Int = 0,
    val requiredSkills: Set<String> = emptySet(),
    var customerId: Long,
    val noteDescription: String? = null
)