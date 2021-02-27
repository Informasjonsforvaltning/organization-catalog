package no.brreg.informasjonsforvaltning.organizationcatalogue.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
class ApplicationStatusController {

    @GetMapping("/ping")
    fun ping(): ResponseEntity<String> =
        ResponseEntity.ok("pong")

    @GetMapping("/ready")
    fun ready(): ResponseEntity<Void> =
        ResponseEntity.ok().build()

}
