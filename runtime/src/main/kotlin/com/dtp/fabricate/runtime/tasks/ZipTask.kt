package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.putRelativeFile
import com.dtp.fabricate.runtime.relativeFiles
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

    // This does not support large files since the compression is done in a single path
    // For larger files this would need to be broken up into chunks.
    private fun compress() {
        val outputFile = File("${root.nameWithoutExtension}.zip")

        if (outputFile.exists()) {
            outputFile.delete()
        }

        outputFile.createNewFile()

        val fileOutputStream = outputFile.outputStream()
        zipStream = ZipOutputStream(fileOutputStream)

        if (root.isDirectory) {
            root.relativeFiles { file, relativePath ->
                zipStream.putRelativeFile(file, relativePath)
            }
        } else {
            zipStream.putRelativeFile(root, root.path)
        }

        zipStream.close()
    }
}