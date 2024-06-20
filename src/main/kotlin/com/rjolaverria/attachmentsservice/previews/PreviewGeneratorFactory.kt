package com.rjolaverria.attachmentsservice.previews

import com.rjolaverria.attachmentsservice.images.ImageScaler
import com.rjolaverria.attachmentsservice.videos.VideoFrameExtractor

class PreviewGeneratorFactory {
    private val imageScaler = ImageScaler()
    private val videoFrameExtractor = VideoFrameExtractor()
    private val imagePreviewGenerator = ImagePreviewGenerator(imageScaler)
    private val videoPreviewGenerator= VideoPreviewGenerator(videoFrameExtractor, imageScaler)
    private val generators: MutableMap<String, PreviewGenerator> = mutableMapOf(
        // Images
        "image/jpg" to imagePreviewGenerator,
        "image/jpeg" to imagePreviewGenerator,
        "image/png" to imagePreviewGenerator,
        "image/gif" to imagePreviewGenerator,
        "image/bmp" to imagePreviewGenerator,
        // Videos
        "video/mp4" to videoPreviewGenerator,
        "video/mpeg" to videoPreviewGenerator,
        "video/x-msvideo" to videoPreviewGenerator,
        "video/quicktime" to videoPreviewGenerator,
        "video/x-ms-wmv" to videoPreviewGenerator
    )

    fun get(mimeType: String?): PreviewGenerator? {
        return generators[mimeType]
    }
}