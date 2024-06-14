package com.rjolaverria.attachmentsservice.storage

import java.io.File
import java.util.UUID
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LocalStorageTest {

    private lateinit var localStorage: LocalStorage
    private val storageDir = "test-storage-dir"

    @BeforeEach
    fun setup() {
        val dir = File(storageDir)
        if (dir.exists()) {
            dir.deleteRecursively()
        }
        dir.mkdirs()

        localStorage = LocalStorage(storageDir)
    }

    @AfterEach
    fun cleanup() {
        val dir = File(storageDir)
        if (dir.exists()) {
            dir.deleteRecursively()
        }
    }

    @Test
    fun `upload should store file and return success`() = runTest {
        val filename = "test.txt"
        val content = "Hello, world!".toByteArray()

        val result = localStorage.upload(filename, content)

        assertTrue(result is StorageUploadResult.Success)
        val successResult = result as StorageUploadResult.Success
        assertEquals(filename, successResult.filename)

        val storedFile = File("$storageDir/${successResult.id}-$filename")
        assertTrue(storedFile.exists())
        assertArrayEquals(content, storedFile.readBytes())
    }

    @Test
    fun `download should return file content if exists`() = runTest {
        val filename = "test.txt"
        val content = "Hello, world!".toByteArray()

        val uploadResult = localStorage.upload(filename, content) as StorageUploadResult.Success
        val id = uploadResult.id.toString()

        val downloadResult = localStorage.download(id)

        assertTrue(downloadResult is StorageDownloadResult.Success)
        val successResult = downloadResult as StorageDownloadResult.Success
        assertArrayEquals(content, successResult.content)
    }

    @Test
    fun `download should return NotFound if file does not exist`() = runTest {
        val nonExistentId = UUID.randomUUID().toString()

        val downloadResult = localStorage.download(nonExistentId)

        assertTrue(downloadResult is StorageDownloadResult.NotFound)
    }

    @Test
    fun `upload should return error if there is an IOException`() = runTest {
        val invalidStorageDir = "/invalid-path"
        val invalidStorage = LocalStorage(invalidStorageDir)
        val filename = "test.txt"
        val content = "Hello, world!".toByteArray()

        val result = invalidStorage.upload(filename, content)

        assertTrue(result is StorageUploadResult.Error)
    }
}
