package com.rjolaverria.attachmentsservice.images

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import org.apache.tika.Tika
import org.apache.tika.mime.MimeTypes


class ImageScaler {
    private val tika = Tika()
    private val mimeTypes = MimeTypes.getDefaultMimeTypes()
    fun isSupported(mimeType: String): Boolean {
        return mimeType.startsWith("image/")
    }

    fun scale(image: ByteArray, width: Int, formatName: String = getFormatName(image)): ByteArray {
        val inputStream = ByteArrayInputStream(image)
        val bufferedImage = ImageIO.read(inputStream)

        if (bufferedImage.width <= width) return image

        val height = getHeight(width, bufferedImage)
        val bufferedScaledImage = scaleImage(bufferedImage, width, height)

        return ByteArrayOutputStream().use {
            ImageIO.write(bufferedScaledImage, formatName, it)
            it.toByteArray()
        }
    }

    private fun scaleImage(originalImage: BufferedImage?, targetWidth: Int, targetHeight: Int): BufferedImage {
        val resizedImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        val graphics2D = resizedImage.createGraphics()
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null)
        graphics2D.dispose()
        return resizedImage
    }

    private fun getHeight(width: Int, bufferedImage: BufferedImage): Int {
        val originalWidth = bufferedImage.width
        val originalHeight = bufferedImage.height
        val aspectRatio = originalHeight.toDouble() / originalWidth.toDouble()
        return (width * aspectRatio).toInt()
    }

    private fun getFormatName(image: ByteArray): String {
        val mimeType = tika.detect(image)
        val mimeTypeObj = mimeTypes.forName(mimeType)
        return mimeTypeObj.extension.removePrefix(".")
    }
}
