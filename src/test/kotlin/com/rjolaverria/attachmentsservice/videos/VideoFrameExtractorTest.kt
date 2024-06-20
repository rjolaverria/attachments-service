package com.rjolaverria.attachmentsservice.videos

import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import org.junit.jupiter.api.assertThrows

class VideoFrameExtractorTest {

    @Test
    fun `test extract frame from video`() {
        // Arrange
        val videoBytes = ByteArray(100) // Mock video byte array
        val formatName = "png"
        val expectedImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
        val expectedImageBytes = ByteArrayOutputStream().use {
            ImageIO.write(expectedImage, formatName, it)
            it.toByteArray()
        }

        val frame = Frame()
        val grabber = mockk<FFmpegFrameGrabber>(relaxed = true)
        val converter = mockk<Java2DFrameConverter>()

        every { grabber.grabImage() } returns frame
        every { converter.convert(frame) } returns expectedImage

        // Mock the constructor of FFmpegFrameGrabber to return our mock instance
        mockkConstructor(FFmpegFrameGrabber::class)
        every { anyConstructed<FFmpegFrameGrabber>().start() } just Runs
        every { anyConstructed<FFmpegFrameGrabber>().grabImage() } returns frame
        every { anyConstructed<FFmpegFrameGrabber>().stop() } just Runs

        // Ensure that Java2DFrameConverter.convert is mocked correctly
        mockkConstructor(Java2DFrameConverter::class)
        every { anyConstructed<Java2DFrameConverter>().convert(frame) } returns expectedImage

        // Use real implementations in the extractor
        val extractor = spyk(VideoFrameExtractor(), recordPrivateCalls = true)

        // Act
        val resultBytes = extractor.extract(videoBytes, formatName)

        // Assert
        assertArrayEquals(expectedImageBytes, resultBytes)

        verify { anyConstructed<FFmpegFrameGrabber>().start() }
        verify { anyConstructed<FFmpegFrameGrabber>().grabImage() }
        verify { anyConstructed<FFmpegFrameGrabber>().stop() }
    }

    @Test
    fun `test extract with empty video`() {
        // Arrange
        val videoBytes = ByteArray(0)
        val formatName = "png"

        val grabber = mockk<FFmpegFrameGrabber>(relaxed = true)
        every { grabber.grabImage() } returns null

        // Mock the constructor of FFmpegFrameGrabber to return our mock instance
        mockkConstructor(FFmpegFrameGrabber::class)
        every { anyConstructed<FFmpegFrameGrabber>().start() } just Runs
        every { anyConstructed<FFmpegFrameGrabber>().grabImage() } returns null
        every { anyConstructed<FFmpegFrameGrabber>().stop() } just Runs

        // Use real implementations in the extractor
        val extractor = spyk(VideoFrameExtractor(), recordPrivateCalls = true)

        // Act & Assert
        assertThrows<Exception> {
            extractor.extract(videoBytes, formatName)
        }

        verify { anyConstructed<FFmpegFrameGrabber>().start() }
        verify { anyConstructed<FFmpegFrameGrabber>().grabImage() }
    }
}
