package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.BUILD_CLASSES_DIR
import com.dtp.fabricate.runtime.BUILD_LIBS_DIR
import com.dtp.fabricate.runtime.deps.buildLocation
import com.dtp.fabricate.runtime.deps.getDependencyCacheDir
import com.dtp.fabricate.runtime.models.Dependency
import com.dtp.fabricate.runtime.models.Project
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission
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
        val outputFile = File(libsDir, "/${project.name}.jar")

        if (!libsDir.exists()) {
            libsDir.mkdirs()
        }

        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }
//        Files.setPosixFilePermissions(
//            outputFile.toPath(),
//            setOf(
//                PosixFilePermission.OTHERS_EXECUTE,
//                PosixFilePermission.OTHERS_WRITE,
//                PosixFilePermission.OTHERS_READ
//            ),
//        )

        val fileOutputStream = outputFile.outputStream()

        val manifest = Manifest().apply {
            mainAttributes[Attributes.Name("Manifest-Version")] = "1.0"
            mainAttributes[Attributes.Name("Main-Class")] = mainClass.replace(".kt", "Kt")
        }

        val jarOutputStream = JarOutputStream(fileOutputStream, manifest)

        addProject(project, jarOutputStream)

        //TODO I think we need to add all dependency class files.

        //TODO Need to add kotlin_module files (not sure how to do this)

        jarOutputStream.close()
    }

    private fun addRemoteFiles(project: Project, jarOutputStream: JarOutputStream) {
        project.dependencyScope?.dependencies?.forEach { dependency ->
            when (dependency) {
                is Dependency.Project -> addProject(project, jarOutputStream)

                is Dependency.Remote -> {
                    val url = buildLocation(dependency.value)

                    val file = "${getDependencyCacheDir()}/${url.cacheKey}/"
                }
            }
        }
    }

    private fun addProject(project: Project, jarOutputStream: JarOutputStream) {
        val inputFile = File("${project.projectDir.path}/$BUILD_CLASSES_DIR")

        val dirs = mutableListOf(inputFile)

        while (dirs.isNotEmpty()) {
            val current = dirs.removeAt(dirs.lastIndex)

            current.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    dirs.add(file)
                } else {
                    val relativePath = file.relativeTo(inputFile).path
                    addFile(file, relativePath, jarOutputStream)
                }
            }
        }
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