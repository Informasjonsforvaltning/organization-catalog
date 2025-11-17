package no.digdir.organizationcatalog.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
class ApplicationStatusController {
    @GetMapping("/ping")
    fun ping(): ResponseEntity<String> = ResponseEntity.ok("pong")

    @GetMapping("/ready")
    fun ready(): ResponseEntity<Void> = ResponseEntity.ok().build()
}
