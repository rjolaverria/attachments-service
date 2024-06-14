package com.rjolaverria.attachmentsservice.previews

import com.rjolaverria.attachmentsservice.images.ImageScaler

class PreviewGeneratorFactory {
    private val imagePreviewGenerator = ImagePreviewGenerator(ImageScaler())
    private val generators: MutableMap<String, PreviewGenerator> = mutableMapOf(
        "image/jpg" to imagePreviewGenerator,
        "image/jpeg" to imagePreviewGenerator,
        "image/png" to imagePreviewGenerator,
        "image/gif" to imagePreviewGenerator,
        "image/bmp" to imagePreviewGenerator,
    )

    fun get(mimeType: String?): PreviewGenerator? {
        return generators[mimeType]
    }
}