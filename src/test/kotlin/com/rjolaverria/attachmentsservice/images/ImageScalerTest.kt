package com.rjolaverria.attachmentsservice.images

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream
import kotlin.test.assertTrue

class ImageScalerTest {

    private val imageScaler = ImageScaler()

    @Test
    fun `test isSupported returns true for supported image types`() {
        assertTrue(imageScaler.isSupported("image/png"))
        assertTrue(imageScaler.isSupported("image/jpeg"))
        assertTrue(imageScaler.isSupported("image/gif"))
    }

    @Test
    fun `test isSupported returns false for unsupported image types`() {
        assertFalse(imageScaler.isSupported("application/pdf"))
        assertFalse(imageScaler.isSupported("text/plain"))
        assertFalse(imageScaler.isSupported("audio/mpeg"))
    }

    @Test
    fun `test scale does not alter image if width is less than or equal to target width`() {
        val originalImage = createTestImage(100, 50)
        val imageBytes = convertImageToBytes(originalImage, "png")

        val scaledImageBytes = imageScaler.scale(imageBytes, 100)

        assertArrayEquals(imageBytes, scaledImageBytes)
    }

    @Test
    fun `test scale reduces image width correctly`() {
        val originalImage = createTestImage(200, 100)
        val imageBytes = convertImageToBytes(originalImage, "png")

        val scaledImageBytes = imageScaler.scale(imageBytes, 100)
        val scaledImage = ImageIO.read(ByteArrayInputStream(scaledImageBytes))

        assertEquals(100, scaledImage.width)
        assertEquals(50, scaledImage.height)
    }

    private fun createTestImage(width: Int, height: Int): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val graphics = image.createGraphics()
        graphics.fillRect(0, 0, width, height)
        graphics.dispose()
        return image
    }

    private fun convertImageToBytes(image: BufferedImage, formatName: String): ByteArray {
        return ByteArrayOutputStream().use {
            ImageIO.write(image, formatName, it)
            it.toByteArray()
        }
    }
}
