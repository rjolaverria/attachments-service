package com.rjolaverria.attachmentsservice.previews

import com.rjolaverria.attachmentsservice.images.ImageScaler

class ImagePreviewGenerator(private val imageScaler: ImageScaler): PreviewGenerator {
    companion object {
        private const val MAX_WIDTH = 200
        private const val OUTPUT_FORMAT = "png"
    }
    override fun generate(content: ByteArray): Preview {
        return Preview(
            content = imageScaler.scale(content, MAX_WIDTH, OUTPUT_FORMAT),
            fileExtension = OUTPUT_FORMAT
        )
    }
}