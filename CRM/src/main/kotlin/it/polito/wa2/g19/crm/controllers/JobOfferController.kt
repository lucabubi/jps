package it.polito.wa2.g19.crm.controllers

import it.polito.wa2.g19.crm.dtos.CreateJobOfferDTO
import it.polito.wa2.g19.crm.dtos.JobOfferDTO
import it.polito.wa2.g19.crm.dtos.JobOfferUpdateDTO
import it.polito.wa2.g19.crm.entities.JobOffer
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import it.polito.wa2.g19.crm.services.JobOfferService



@RestController
@RequestMapping("/API/joboffers")
class JobOfferController(private val jobOfferService: JobOfferService) {

    @PostMapping("/")
    fun createJobOffer(@RequestBody createJobOfferDTO: CreateJobOfferDTO) : ResponseEntity<JobOfferDTO> {
        val newJobOffer = jobOfferService.createJobOffer(createJobOfferDTO)
        return ResponseEntity.ok(newJobOffer)
    }

    @GetMapping("/")
    fun getJobOffers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) customerId: Long?,
        @RequestParam(required = false) status: JobOffer.Status?,
        @RequestParam(required = false) professionalId: Long?
    ) : ResponseEntity<List<JobOfferDTO>> {
        val pageable = PageRequest.of(page, size)
        val jobOffers = jobOfferService.getJobOffers(pageable, customerId, status, professionalId)
        return ResponseEntity.ok(jobOffers)
    }

    @GetMapping("/open/{customerId}")
    fun getOpenJobOffers(
        @PathVariable customerId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int) : ResponseEntity<Any> {
        val pageable: Pageable = try {
            PageRequest.of(page, size)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body("Invalid page or size")
        }
        val openJobOffers = jobOfferService.getOpenJobOffers(customerId, pageable)
        return ResponseEntity.ok(openJobOffers)
    }

    @GetMapping("/accepted/{professionalId}")
    fun getAcceptedJobOffers(
        @PathVariable professionalId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int) : ResponseEntity<Any> {
        val pageable: Pageable = try {
            PageRequest.of(page, size)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body("Invalid page or size")
        }
        val acceptedJobOffers = jobOfferService.getAcceptedJobOffers(professionalId, pageable)
        return ResponseEntity.ok(acceptedJobOffers)
    }

    @GetMapping("/aborted/")
    fun getAbortedJobOffers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) customerId: Long?,
        @RequestParam(required = false) professionalId: Long?) : ResponseEntity<Any> {
        val pageable: Pageable = try {
            PageRequest.of(page, size)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body("Invalid page or size")
        }
        val abortedJobOffers = jobOfferService.getAbortedJobOffers(pageable, customerId, professionalId)
        return ResponseEntity.ok(abortedJobOffers)
    }

    @PostMapping("/{jobOfferId}")
    fun changeJobOfferStatus(@PathVariable jobOfferId: Long, @RequestBody requestDTO: JobOfferUpdateDTO) : ResponseEntity<JobOfferDTO> {
        val updateJobOffer = jobOfferService.updateJobOffer(jobOfferId, requestDTO)
        return ResponseEntity.ok(updateJobOffer)
    }

    @GetMapping("/{jobOfferId}/value")
    fun getJobOfferValue(@PathVariable jobOfferId: Long) : ResponseEntity<Float> {
        val jobOffersValue = jobOfferService.getJobOfferValue(jobOfferId)
        return ResponseEntity.ok(jobOffersValue)
    }
}