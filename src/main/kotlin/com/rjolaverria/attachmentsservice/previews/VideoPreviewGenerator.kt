package com.rjolaverria.attachmentsservice.previews

import com.rjolaverria.attachmentsservice.images.ImageScaler
import com.rjolaverria.attachmentsservice.videos.VideoFrameExtractor

class VideoPreviewGenerator(
    private val videoFrameExtractor: VideoFrameExtractor,
    private val imageScaler: ImageScaler
): PreviewGenerator {
    companion object {
        private const val MAX_WIDTH = 200
        private const val OUTPUT_FORMAT = "png"
    }
    override fun generate(content: ByteArray): Preview {
        val frame = videoFrameExtractor.extract(content, OUTPUT_FORMAT)
        val image =  imageScaler.scale(frame, MAX_WIDTH, OUTPUT_FORMAT)
        return Preview(
            content = image,
            fileExtension = OUTPUT_FORMAT
        )
    }
}