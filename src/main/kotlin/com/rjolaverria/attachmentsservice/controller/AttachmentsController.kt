package com.rjolaverria.attachmentsservice.controller

import com.rjolaverria.attachmentsservice.service.AttachmentsService
import com.rjolaverria.attachmentsservice.service.GetAttachmentResult
import com.rjolaverria.attachmentsservice.service.PutAttachmentResult
import kotlinx.coroutines.runBlocking
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/attachment")
class AttachmentsController(val attachmentsService: AttachmentsService) {

    @GetMapping("/{id}")
    fun getAttachment(@PathVariable("id") id: String) = runBlocking {
        when (val res = attachmentsService.get(id)) {
            is GetAttachmentResult.Success -> {
                val resource: Resource = ByteArrayResource(res.content)
                ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$id")
                    .body(resource)
            }
            is GetAttachmentResult.NotFound -> ResponseEntity.notFound().build()
            is GetAttachmentResult.Error -> ResponseEntity.badRequest().body(res.message)
        }
    }

    @PostMapping("/upload")
    fun uploadAttachment(@RequestParam("file") file: MultipartFile) = runBlocking {
        val filename = file.originalFilename ?: throw IllegalArgumentException("Invalid file")
        when (val res = attachmentsService.put(filename, file.bytes)) {
            is PutAttachmentResult.Success -> {
                ResponseEntity.ok()
                    .header( HttpHeaders.CONTENT_TYPE, "application/json")
                    .body(res)
            }
            is PutAttachmentResult.Error -> ResponseEntity.badRequest().body(res.message)
        }
    }
}