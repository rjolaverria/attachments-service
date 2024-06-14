package com.rjolaverria.attachmentsservice.previews

fun interface PreviewGenerator {

    /**
     * Generates a preview of the given content.
     */
    fun generate(content: ByteArray): ByteArray
}