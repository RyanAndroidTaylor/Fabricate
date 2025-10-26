package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.BUILD_CLASSES_DIR
import com.dtp.fabricate.runtime.BUILD_LIBS_DIR
import java.io.File
import java.util.jar.Attributes
import java.util.jar.JarOutputStream
import java.util.jar.Manifest
import java.util.zip.CRC32
import java.util.zip.ZipEntry

class JarTask : AbstractTask() {
    lateinit var mainClass: String

    override fun execute() {
        println("Generating Jar for ${project.name}...")

        val libsDir = File("${project.projectDir.path}/$BUILD_LIBS_DIR")

        if (!libsDir.exists()) {
            libsDir.mkdirs()
        }

        val inputFile = File("${project.projectDir.path}/$BUILD_CLASSES_DIR")
        val outputFile = File(libsDir,"/${project.name}.jar")

        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }

        val fileOutputStream = outputFile.outputStream()

        val manifest = Manifest().apply {
            mainAttributes[Attributes.Name("Manifest-Version")] = "1.0"
            mainAttributes[Attributes.Name("Main-Class")] = mainClass.replace(".kt", "Kt")
        }

        val jarOutputStream = JarOutputStream(fileOutputStream, manifest)

        val dirs = mutableListOf(inputFile)

        while (dirs.isNotEmpty()) {
            val current = dirs.removeAt(dirs.lastIndex)

            current.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    if (file.name != "META-INF") {
                        dirs.add(file)
                    }
                } else {
                    val relativePath = file.relativeTo(inputFile).path
                    addFile(file, relativePath, jarOutputStream)
                }
            }
        }

        jarOutputStream.close()
    }

    private fun addFile(file: File, relativePath: String, jar: JarOutputStream) {
        val bytes = file.readBytes()

        val entry = ZipEntry(relativePath).apply {
            val crc32 = CRC32()
            crc32.update(bytes)

            crc = crc32.value
            method = ZipEntry.STORED
            size = bytes.size.toLong()
        }

        jar.putNextEntry(entry)
        jar.write(bytes)
        jar.closeEntry()
    }
}