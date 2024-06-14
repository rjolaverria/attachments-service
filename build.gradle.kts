val awsS3Version = "1.0.0"
val awsDynamoDBVersion = "1.0.0"
val okhttpVersion = "5.0.0-alpha.14"
val dotenvVersion = "0.0.2"
val mockkVersion = "1.13.11"
val tikaVersion = "2.9.2"

plugins {
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("plugin.jpa") version "1.9.24"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
}

group = "com.rjolaverria"
version = "0.0.1-SNAPSHOT"


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("aws.sdk.kotlin:s3:$awsS3Version") {
        exclude("com.squareup.okhttp3:okhttp")
    }
    implementation("aws.sdk.kotlin:dynamodb:$awsDynamoDBVersion") {
        exclude("com.squareup.okhttp3:okhttp")
    }

    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.github.dotenv-org:dotenv-vault-kotlin:$dotenvVersion")
    implementation ("org.reactivestreams:reactive-streams")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.apache.tika:tika-core:$tikaVersion")
    implementation("org.apache.tika:tika-parsers-standard-package:$tikaVersion")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
