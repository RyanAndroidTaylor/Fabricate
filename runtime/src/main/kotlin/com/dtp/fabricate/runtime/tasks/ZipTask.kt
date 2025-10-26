package com.dtp.fabricate.runtime.tasks

import java.io.File
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ZipTask : AbstractTask() {

    lateinit var root: File

    lateinit var zipStream: ZipOutputStream

    override fun execute() {
        println("Zipping...")

        compress()
    }

    private fun compress() {
        val outputFile = File("${root.nameWithoutExtension}.zip")

        if (outputFile.exists()) {
            outputFile.delete()
        }

        outputFile.createNewFile()

        val fileOutputStream = outputFile.outputStream()
        zipStream = ZipOutputStream(fileOutputStream)

        val dirs = mutableListOf<File>()

        if (root.isDirectory) {
            dirs.add(root)
        } else {
            compressFile(root)
        }

        while (dirs.isNotEmpty()) {
            val current = dirs.removeAt(dirs.lastIndex)

            current.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    dirs.add(file)
                } else {
                    compressFile(file)
                }
            }
        }

        zipStream.close()
    }

    // As of right now this does not support extra large files. The file content is read, compressed and written
    // in a single pass so extra larges files could run into memory issues
    private fun compressFile(file: File) {
        val discardRange = root.absolutePath.substring(0, root.absolutePath.length - this@ZipTask.root.name.length).length

        val localQualifiedName = file.absolutePath.substring(discardRange)

        println("Compressing file: $localQualifiedName")

        val bytes = file.readBytes()

        val entry = ZipEntry(localQualifiedName).apply {
            val crc32 = CRC32()
            crc32.update(bytes)

            crc = crc32.value
            method = ZipEntry.STORED
            size = bytes.size.toLong()
        }

        zipStream.putNextEntry(entry)
        zipStream.write(bytes)
        zipStream.closeEntry()

        println("$localQualifiedName completed")
    }
}