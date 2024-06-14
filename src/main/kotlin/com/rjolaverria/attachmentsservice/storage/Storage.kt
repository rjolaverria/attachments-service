package com.rjolaverria.attachmentsservice.storage

import java.util.UUID

interface Storage {
    /**
     * Uploads a file to the storage
     * @param filename the name of the file
     * @param content the content of the file
     * @return the URL of the file
     */
    suspend fun upload(filename: String, content: ByteArray): StorageUploadResponse

    /**
     * Downloads a file from the storage
     * @param filename the name of the file
     * @return the content of the file
     */
    suspend fun download(id: String): StorageDownloadResponse
}

sealed class StorageUploadResponse {
    data class Success(val filename: String, val id: UUID): StorageUploadResponse()
    data class Error(val message: String): StorageUploadResponse()
}

sealed class StorageDownloadResponse {
    data class Success(val content: ByteArray): StorageDownloadResponse()
    object NotFound: StorageDownloadResponse()
    data class Error(val message: String): StorageDownloadResponse()
}