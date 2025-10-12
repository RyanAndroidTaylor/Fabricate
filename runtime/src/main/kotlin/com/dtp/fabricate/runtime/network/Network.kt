package com.dtp.fabricate.runtime.network

import java.net.URL

class Network {
    //TODO Need detailed error handling and logging
    fun download(url: URL): ByteArray {
        println("Opening connection to: $url")
        val connection = url.openConnection()

        val inputStream = connection.getInputStream()
        val length = connection.contentLength

        println("Expected Length: $length")

        val bytes = inputStream.readBytes()

        println("Actual Size: ${bytes.size}")

        return bytes
    }
}