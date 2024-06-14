package com.rjolaverria.attachmentsservice.previews

import com.rjolaverria.attachmentsservice.images.ImageScaler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ImagePreviewGeneratorTest {

    private lateinit var imagePreviewGenerator: ImagePreviewGenerator
    private val imageScaler = mockk<ImageScaler>()

    @BeforeEach
    fun setup() {
        imagePreviewGenerator = ImagePreviewGenerator(imageScaler)
    }

    @Test
    fun `test generate scales image to max width`() {
        val originalImage = createTestImage(400, 200)
        val imageBytes = convertImageToBytes(originalImage, "png")
        val scaledImageBytes = convertImageToBytes(createTestImage(200, 100), "png")

        coEvery { imageScaler.scale(any<ByteArray>(), any<Int>()) } returns scaledImageBytes

        val result = imagePreviewGenerator.generate(imageBytes)

        assertArrayEquals(scaledImageBytes, result)
        coVerify { imageScaler.scale(imageBytes, 200) }
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