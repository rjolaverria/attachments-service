package com.rjolaverria.attachmentsservice.storage

import java.io.File
import java.io.IOException
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalStorage(
    private val storageDir: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Storage {

    init {
        val dir = File(storageDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    override suspend fun upload(filename: String, content: ByteArray): StorageUploadResult {
        return withContext(dispatcher) {
            try {
                val id = UUID.randomUUID()
                val file = File("$storageDir/$id-$filename")
                file.writeBytes(content)
                StorageUploadResult.Success(filename, id)
            } catch (e: IOException) {
                StorageUploadResult.Error(e.message ?: "Unknown error")
            }
        }
    }

    override suspend fun download(id: String): StorageDownloadResult {
        return withContext(dispatcher) {
            try {
                val file = File(storageDir).listFiles()?.find { it.name.startsWith(id) }
                if (file != null && file.exists()) {
                    StorageDownloadResult.Success(file.readBytes())
                } else {
                    StorageDownloadResult.NotFound
                }
            } catch (e: IOException) {
                StorageDownloadResult.Error(e.message ?: "Unknown error")
            }
        }
    }
}
