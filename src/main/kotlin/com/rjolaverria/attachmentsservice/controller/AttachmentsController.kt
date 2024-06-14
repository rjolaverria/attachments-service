package com.rjolaverria.attachmentsservice.controller

import com.rjolaverria.attachmentsservice.storage.StorageDownloadResponse
import com.rjolaverria.attachmentsservice.storage.StorageFactory
import com.rjolaverria.attachmentsservice.storage.StorageUploadResponse
import kotlinx.coroutines.runBlocking
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/attachment")
class AttachmentsController {
    private val storage = StorageFactory.createStorage()

    @GetMapping("/{id}")
    fun getAttachment(@PathVariable("id") id: String) = runBlocking {
        when (val res = storage.download(id)) {
            is StorageDownloadResponse.Success -> {
                val resource: Resource = ByteArrayResource(res.content)
                ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$id")
                    .body(resource)
            }
            is StorageDownloadResponse.NotFound -> ResponseEntity.notFound().build()
            is StorageDownloadResponse.Error -> ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/upload")
    fun uploadAttachment(@RequestParam("file") file: MultipartFile) = runBlocking {
        val filename = file.originalFilename ?: throw IllegalArgumentException("Invalid file")
        val res = storage.upload(filename, file.bytes)
        println(res)
        when (res) {
            is StorageUploadResponse.Success -> {
                ResponseEntity.ok()
                    .header( HttpHeaders.CONTENT_TYPE, "application/json")
                    .body(res)
            }
            is StorageUploadResponse.Error -> ResponseEntity.badRequest().build()
        }
    }
}