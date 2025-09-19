package it.polito.wa2.g19.crm.controllers

import it.polito.wa2.g19.crm.dtos.ProfessionalDTO
import it.polito.wa2.g19.crm.dtos.ProfessionalUpdateDTO
import it.polito.wa2.g19.crm.entities.Professional
import it.polito.wa2.g19.crm.services.CRMService
import org.springframework.data.domain.PageRequest
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
        val pageable = PageRequest.of(page, size)
        val professionalsDTO = crmService.getProfessionals(pageable, employmentState, location, skills)
        return ResponseEntity.ok(professionalsDTO)
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