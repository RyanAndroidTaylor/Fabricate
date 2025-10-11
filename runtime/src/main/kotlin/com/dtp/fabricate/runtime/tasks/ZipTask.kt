package com.dtp.fabricate.runtime.tasks

import java.io.File
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ZipTask(val file: File) : Task {

    override fun run() {
        println("ZipFile: ${file.absolutePath}")
        compress(file)
    }

    //TODO Figured out how to name the entries. If they are in a sub-directory do we need to add that to the name?
    // Sounds like the directory information is stored in the name of the file example/data/file.txt
    private fun compress(root: File) {
//        val outputFile = File("${root.nameWithoutExtension}.zip")
        val outputFile = File("/Users/ryantaylor/Desktop/build.fabricate.zip")

        if (outputFile.exists()) {
            outputFile.delete()
        }

        outputFile.createNewFile()

        val fileOutputStream = outputFile.outputStream()
        val zipStream = ZipOutputStream(fileOutputStream)

        val discardRange = root.absolutePath.substring(0, root.absolutePath.length - root.name.length).length
        val dirs = mutableListOf<File>()

        if (root.isDirectory) {
            dirs.add(root)
        } else {
            //TODO Obviously this would not work with huges files since it read the entire contents of the file all at once
            val bytes = root.readBytes()

            val entry = ZipEntry(root.name).apply {
                val crc32 = CRC32()
                crc32.update(bytes)

                crc = crc32.value
                method = ZipEntry.STORED
                size = bytes.size.toLong()
            }

            zipStream.putNextEntry(entry)
            zipStream.write(bytes)
            zipStream.closeEntry()
            zipStream.close()
        }

        while (dirs.isNotEmpty()) {
            val current = dirs.removeAt(dirs.lastIndex)
            println("Full Path: ${current.absolutePath}")
            val currentPath = current.absolutePath.substring(discardRange)

            println("CurrentPath: $currentPath")

            current.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    dirs.add(file)
                } else {
                    println("Add file: $currentPath/${file.name}")

                    val entry = ZipEntry("$currentPath/${file.name}")
                    entry.size
                }
            }
        }
    }
//        val entry = ZipEntry("SomeFile")
//        entry.size = 100
//        entry.isDirectory
//        outputStream.putNextEntry(entry)
}

class DirInputStream {

}

// Old simple compress
//println("Starting ZipTask")
//
//if (!file.isFile) {
//    println("Invalid file ${file.absolutePath}")
//
//    return
//}
//
//println("Reading file...")
//val fileName = file.nameWithoutExtension
//val bytes = file.inputStream().readBytes()
//val output = ByteArray(bytes.size)
//
//println("Compressing file...")
//with(Deflater()) {
//    setInput(bytes)
//    finish()
//    deflate(output)
//    end()
//}
//
//val zipFile = File("${file.parentFile.absolutePath}/$fileName.zip")
//
//if (!zipFile.exists()) {
//    zipFile.createNewFile()
//}
//
//println("Writing file...")
//zipFile.writeBytes(output)
