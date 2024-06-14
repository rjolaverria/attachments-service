package com.rjolaverria.attachmentsservice.storage

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.s3.S3Client
import com.rjolaverria.attachmentsservice.config.Env

class StorageFactory {
    companion object {
        fun createStorage(): Storage {
            return when (Env.env) {
                "dev" -> LocalStorage("./uploads")
                else -> AWSStorage(
                    S3Client { region = Env.awsRegion },
                    DynamoDbClient { region = Env.awsRegion }
                )
            }
        }
    }
}