package com.rjolaverria.attachmentsservice.storage

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.toByteArray
import com.rjolaverria.attachmentsservice.config.Env
import org.slf4j.LoggerFactory
import java.util.UUID

class AWSStorage(private val s3Client: S3Client, private val dynamoClient: DynamoDbClient) : Storage {
    private val logger = LoggerFactory.getLogger(AWSStorage::class.java)

    companion object {
        private const val dynamoKey = "id"
        private const val dynamoValue = "s3key"
    }
    private val s3bucketName = Env.s3BucketName
    private val dynamoTableName = Env.dynamoTableName

    override suspend fun upload(filename: String, content: ByteArray): StorageUploadResult {
        val url = putObject(filename, content) ?: return StorageUploadResult.Error("Error uploading file")
        val id = putObjectKey(url) ?: return StorageUploadResult.Error("Error uploading file")
        return StorageUploadResult.Success(filename, id)
    }

    override suspend fun download(id: String): StorageDownloadResult {
        val s3Key = getObjectKey(id) ?: return StorageDownloadResult.NotFound
        val bytes = getObject(s3Key)
        return if (bytes != null) {
            StorageDownloadResult.Success(bytes)
        } else {
            StorageDownloadResult.Error("Error downloading file")
        }
    }

    private suspend fun getObjectKey(id: String): String? {
        val request = GetItemRequest {
            key = mapOf(dynamoKey to AttributeValue.S(id))
            tableName = dynamoTableName
        }

        return try {
            dynamoClient.getItem(request).item?.get(dynamoValue)?.asS()
        } catch (e: Exception) {
            logger.error("Error getting object key in DynamoDB: $id", e)
            return null
        }
    }

    private suspend fun putObjectKey(s3key: String): UUID? {
        val id = UUID.randomUUID()
        val request = PutItemRequest {
            tableName = dynamoTableName
            item = mapOf(
                dynamoKey to AttributeValue.S(id.toString()),
                dynamoValue to AttributeValue.S(s3key)
            )
        }

        return try {
            dynamoClient.putItem(request)
            id
        } catch (e: Exception) {
            logger.error("Error storing object key in DynamoDB: $s3key", e)
            return null
        }
    }

    private suspend fun putObject(filename: String, content: ByteArray): String? {
        val metadataVal = mutableMapOf<String, String>()
        metadataVal["filename"] = filename

        val request = PutObjectRequest {
            key = filename
            bucket = s3bucketName
            body = ByteStream.fromBytes(content)
            metadata = metadataVal
        }

        return try {
            s3Client.putObject(request).let {
                filename
            }
        } catch (e: Exception) {
            logger.error("Error uploading object to S3: $filename", e)
            return null
        }
    }

    private suspend fun getObject(s3Key: String): ByteArray? {
        val request = GetObjectRequest {
            key = s3Key
            bucket = s3bucketName
        }

        return try {
            s3Client.getObject(request) { response ->
                response.body?.toByteArray()
            }
        } catch (e: Exception) {
            logger.error("Error downloading object from S3: $s3Key", e)
            null
        }
    }

}