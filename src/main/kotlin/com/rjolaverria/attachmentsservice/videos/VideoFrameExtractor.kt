package com.rjolaverria.attachmentsservice.videos

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.Java2DFrameConverter


class VideoFrameExtractor {
    private val converter = Java2DFrameConverter()
    fun extract(video: ByteArray, formatName: String = "png"): ByteArray {
        val grabber = FFmpegFrameGrabber(ByteArrayInputStream(video))
        grabber.start()

        val frame: Frame = grabber.grabImage()
        val bi: BufferedImage = converter.convert(frame)

        grabber.stop()

        return ByteArrayOutputStream().use {
            ImageIO.write(bi, formatName, it)
            it.toByteArray()
        }
    }
}
