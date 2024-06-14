package com.rjolaverria.attachmentsservice.storage

import java.util.UUID

interface Storage {
    /**
     * Uploads a file to the storage
     */
    suspend fun upload(filename: String, content: ByteArray): StorageUploadResult

    /**
     * Downloads a file from the storage
     */
    suspend fun download(id: String): StorageDownloadResult
}

sealed class StorageUploadResult {
    data class Success(val filename: String, val id: UUID): StorageUploadResult()
    data class Error(val message: String): StorageUploadResult()
}

sealed class StorageDownloadResult {
    data class Success(val content: ByteArray): StorageDownloadResult()
    data object NotFound: StorageDownloadResult()
    data class Error(val message: String): StorageDownloadResult()
}