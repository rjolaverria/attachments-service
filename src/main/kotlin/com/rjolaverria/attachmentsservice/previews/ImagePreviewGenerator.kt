package com.rjolaverria.attachmentsservice.previews

import com.rjolaverria.attachmentsservice.images.ImageScaler

class ImagePreviewGenerator(private val imageScaler: ImageScaler): PreviewGenerator {
    companion object {
        private const val MAX_WIDTH = 200
    }
    override fun generate(content: ByteArray): ByteArray {
        return imageScaler.scale(content, MAX_WIDTH)
    }
}