package com.rjolaverria.attachmentsservice.storage

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.util.UUID

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

        val response = localStorage.upload(filename, content)

        assertTrue(response is StorageUploadResponse.Success)
        val successResponse = response as StorageUploadResponse.Success
        assertEquals(filename, successResponse.filename)

        val storedFile = File("$storageDir/${successResponse.id}-$filename")
        assertTrue(storedFile.exists())
        assertArrayEquals(content, storedFile.readBytes())
    }

    @Test
    fun `download should return file content if exists`() = runTest {
        val filename = "test.txt"
        val content = "Hello, world!".toByteArray()

        val uploadResponse = localStorage.upload(filename, content) as StorageUploadResponse.Success
        val id = uploadResponse.id.toString()

        val downloadResponse = localStorage.download(id)

        assertTrue(downloadResponse is StorageDownloadResponse.Success)
        val successResponse = downloadResponse as StorageDownloadResponse.Success
        assertArrayEquals(content, successResponse.content)
    }

    @Test
    fun `download should return NotFound if file does not exist`() = runTest {
        val nonExistentId = UUID.randomUUID().toString()

        val downloadResponse = localStorage.download(nonExistentId)

        assertTrue(downloadResponse is StorageDownloadResponse.NotFound)
    }

    @Test
    fun `upload should return error if there is an IOException`() = runTest {
        val invalidStorageDir = "/invalid-path"
        val invalidStorage = LocalStorage(invalidStorageDir)
        val filename = "test.txt"
        val content = "Hello, world!".toByteArray()

        val response = invalidStorage.upload(filename, content)

        assertTrue(response is StorageUploadResponse.Error)
    }
}
