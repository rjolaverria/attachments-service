package com.rjolaverria.attachmentsservice.service

import java.util.UUID

sealed class PutAttachmentResult {
    data class Success(
        val filename: String,
        val id: UUID,
        val previewName: String? = null,
        val previewId: UUID? = null
    ) : PutAttachmentResult()

    data class Error(val message: String) : PutAttachmentResult()
}

sealed class GetAttachmentResult {
    data class Success(val content: ByteArray): GetAttachmentResult()
    data object NotFound: GetAttachmentResult()
    data class Error(val message: String): GetAttachmentResult()
}