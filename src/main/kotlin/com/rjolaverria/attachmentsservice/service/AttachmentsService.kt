package com.rjolaverria.attachmentsservice.service

import com.rjolaverria.attachmentsservice.images.ImageScaler
import com.rjolaverria.attachmentsservice.previews.PreviewGeneratorFactory
import com.rjolaverria.attachmentsservice.storage.StorageDownloadResult
import com.rjolaverria.attachmentsservice.storage.StorageFactory
import com.rjolaverria.attachmentsservice.storage.StorageUploadResult
import org.apache.commons.compress.utils.FileNameUtils
import org.apache.commons.io.FilenameUtils
import org.apache.tika.Tika
import org.springframework.stereotype.Service

@Service
class AttachmentsService {
    companion object {
        private const val MAX_IMAGE_WIDTH = 2000
    }

    private val storage = StorageFactory.createStorage()
    private val previewGenerator = PreviewGeneratorFactory()
    private val imageScaler = ImageScaler()
    private val tika = Tika()

    suspend fun get(id: String) = when (val res = storage.download(id)) {
            is StorageDownloadResult.Success-> GetAttachmentResult.Success(res.content)
            is StorageDownloadResult.NotFound -> GetAttachmentResult.NotFound
            is StorageDownloadResult.Error -> GetAttachmentResult.Error(res.message)
        }


    suspend fun put(filename: String, content: ByteArray): PutAttachmentResult {
        val baseName = FilenameUtils.getBaseName(filename)
        val mimeType = tika.detect(content)
        val generator = previewGenerator.get(mimeType)
        val preview = generator?.generate(content)
        val previewRes = preview?.let { storage.upload("preview-$baseName.${it.fileExtension}", it.content) }

        val mainContent = if(imageScaler.isSupported(mimeType)) {
            imageScaler.scale(content, MAX_IMAGE_WIDTH)
        } else {
            content
        }

        return when (val res = storage.upload(filename, mainContent)) {
            is StorageUploadResult.Success -> {
                val previewInfo = (previewRes as? StorageUploadResult.Success)?.let {
                    it.id to it.filename
                }
                PutAttachmentResult.Success(
                    id = res.id,
                    filename = filename,
                    previewId = previewInfo?.first,
                    previewName = previewInfo?.second,
                )
            }
            is StorageUploadResult.Error -> PutAttachmentResult.Error(res.message)
        }
    }
}