package com.rjolaverria.attachmentsservice.config

import org.dotenv.vault.dotenvVault

object Env {
    private val dotenv = dotenvVault()

    val env: String = dotenv["ENV"] ?: "dev"

    val s3BucketName: String = dotenv["S3_BUCKET_NAME"] ?: ""

    val awsRegion: String = dotenv["AWS_REGION"] ?: "us-east-1"

    val dynamoTableName: String = dotenv["DYNAMODB_TABLE_NAME"] ?: ""
}