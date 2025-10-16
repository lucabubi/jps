package it.polito.wa2.g19.crm.controllers

import it.polito.wa2.g19.crm.dtos.ProfessionalDTO
import it.polito.wa2.g19.crm.dtos.ProfessionalUpdateDTO
import it.polito.wa2.g19.crm.entities.Professional
import it.polito.wa2.g19.crm.services.CRMService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/API/professionals")
class ProfessionalsController(private val crmService: CRMService) {
    @PostMapping("/")
    fun createProfessional(@RequestBody professional: ProfessionalDTO): ResponseEntity<ProfessionalDTO> {
        val createdProfessional = crmService.createProfessional(professional)
        return ResponseEntity.ok(createdProfessional)
    }

    @GetMapping("/")
    fun getProfessionals(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) employmentState: Optional<Professional.State>,
        @RequestParam(required = false) location: Optional<String>,
        @RequestParam(required = false) skills: Optional<List<String>>
    ) : ResponseEntity<List<ProfessionalDTO>> {
        val all = crmService.getProfessionals(Pageable.unpaged(), employmentState, location, skills)
        val total = all.size

        val from = (page * size).coerceAtMost(total)
        val to = (from + size).coerceAtMost(total)
        val content = if (from < to) all.subList(from, to) else emptyList()

        // header X-Total-Count for frontend to know the total number of items and implement pagination buttons
        return ResponseEntity.ok()
            .header("X-Total-Count", total.toString())
            .body(content)
    }

    @GetMapping("/{professionalId}")
    fun getProfessional(@PathVariable professionalId: Long): ResponseEntity<ProfessionalDTO> {
        val professional = crmService.getProfessional(professionalId)
        return ResponseEntity.ok(professional)
    }

    @PutMapping("/{id}")
    fun updateProfessional(@PathVariable id: Long, @RequestBody updateDTO: ProfessionalUpdateDTO) : ResponseEntity<ProfessionalDTO> {
        val professionalDTO = crmService.updateProfessional(id, updateDTO)
        return ResponseEntity.ok(professionalDTO)
    }
}