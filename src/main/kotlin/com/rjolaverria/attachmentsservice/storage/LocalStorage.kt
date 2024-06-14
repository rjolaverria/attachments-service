package com.rjolaverria.attachmentsservice.storage

import java.io.File
import java.io.IOException
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalStorage(private val storageDir: String) : Storage {

    init {
        val dir = File(storageDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    override suspend fun upload(filename: String, content: ByteArray): StorageUploadResponse {
        return withContext(Dispatchers.IO) {
            try {
                val id = UUID.randomUUID()
                val file = File("$storageDir/$id-$filename")
                file.writeBytes(content)
                StorageUploadResponse.Success(filename, id)
            } catch (e: IOException) {
                StorageUploadResponse.Error(e.message ?: "Unknown error")
            }
        }
    }

    override suspend fun download(id: String): StorageDownloadResponse {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(storageDir).listFiles()?.find { it.name.startsWith(id) }
                if (file != null && file.exists()) {
                    StorageDownloadResponse.Success(file.readBytes())
                } else {
                    StorageDownloadResponse.NotFound
                }
            } catch (e: IOException) {
                StorageDownloadResponse.Error(e.message ?: "Unknown error")
            }
        }
    }
}
