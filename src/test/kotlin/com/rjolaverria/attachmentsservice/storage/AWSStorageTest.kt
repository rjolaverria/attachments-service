package com.rjolaverria.attachmentsservice.storage

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.GetItemResponse
import aws.sdk.kotlin.services.dynamodb.model.PutItemResponse
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectResponse
import aws.sdk.kotlin.services.s3.model.PutObjectResponse
import aws.smithy.kotlin.runtime.content.ByteStream
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.util.UUID
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AWSStorageTest {

    private lateinit var awsStorage: AWSStorage
    private val s3Client = mockk<S3Client>()
    private val dynamoClient = mockk<DynamoDbClient>()

    @BeforeEach
    fun setup() {
        awsStorage = AWSStorage(s3Client, dynamoClient)
    }

    @Test
    fun `upload should store file and return success`() = runTest {
        val filename = "test.txt"
        val content = "Hello, world!".toByteArray()

        coEvery { s3Client.putObject(any()) } returns PutObjectResponse {}
        coEvery { dynamoClient.putItem(any()) } returns PutItemResponse {}

        val response = awsStorage.upload(filename, content)

        assertTrue(response is StorageUploadResult.Success)
        assertEquals(filename, response.filename)
        assertNotNull(response.id)

        coVerify(exactly = 1) { s3Client.putObject(any()) }
        coVerify(exactly = 1) { dynamoClient.putItem(any()) }
    }

    @Test
    fun `download should return file content if exists`() = runTest {
        val id = UUID.randomUUID().toString()
        val s3Key = "test.txt"
        val content = "Hello, world!".toByteArray()

        coEvery { dynamoClient.getItem(any()) } returns GetItemResponse {
            item = mapOf("s3key" to AttributeValue.S(s3Key))
        }
        coEvery { s3Client.getObject(any(), any<suspend (GetObjectResponse) -> ByteArray>()) } coAnswers {
            secondArg<suspend (GetObjectResponse) -> ByteArray>().invoke(GetObjectResponse {
                body = ByteStream.fromBytes(content)
            })
            content
        }

        val response = awsStorage.download(id)

        assertTrue(response is StorageDownloadResult.Success)
        assertContentEquals(content, response.content)

        coVerify(exactly = 1) { dynamoClient.getItem(any()) }
        coVerify(exactly = 1) { s3Client.getObject(any(), any()) }
    }

    @Test
    fun `download should return NotFound if file does not exist`() = runTest {
        val id = UUID.randomUUID().toString()

        coEvery { dynamoClient.getItem(any()) } returns GetItemResponse {
            item = null
        }

        val response = awsStorage.download(id)

        assertTrue(response is StorageDownloadResult.NotFound)

        coVerify(exactly = 1) { dynamoClient.getItem(any()) }
        coVerify(exactly = 0) { s3Client.getObject(any(), any()) }
    }

    @Test
    fun `upload should return error if there is an S3 exception`() = runTest {
        val filename = "test.txt"
        val content = "Hello, world!".toByteArray()

        coEvery { s3Client.putObject(any()) } throws Exception("S3 error")

        val response = awsStorage.upload(filename, content)

        assertTrue(response is StorageUploadResult.Error)

        coVerify(exactly = 1) { s3Client.putObject(any()) }
        coVerify(exactly = 0) { dynamoClient.putItem(any()) }
    }

    @Test
    fun `upload should return error if there is a DynamoDB exception`() = runTest {
        val filename = "test.txt"
        val content = "Hello, world!".toByteArray()

        coEvery { s3Client.putObject(any()) } returns PutObjectResponse {}
        coEvery { dynamoClient.putItem(any()) } throws Exception("DynamoDB error")

        val response = awsStorage.upload(filename, content)

        assertTrue(response is StorageUploadResult.Error)

        coVerify(exactly = 1) { s3Client.putObject(any()) }
        coVerify(exactly = 1) { dynamoClient.putItem(any()) }
    }
}
