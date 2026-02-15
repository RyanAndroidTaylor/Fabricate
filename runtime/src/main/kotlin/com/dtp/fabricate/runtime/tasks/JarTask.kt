package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.BUILD_CLASSES_DIR
import com.dtp.fabricate.runtime.BUILD_LIBS_DIR
import com.dtp.fabricate.runtime.deps.buildLocation
import com.dtp.fabricate.runtime.deps.getDependencyCacheDir
import com.dtp.fabricate.runtime.models.Dependency
import com.dtp.fabricate.runtime.models.Project
import com.dtp.fabricate.runtime.relativeFiles
import java.io.File
import java.util.jar.Attributes
import java.util.jar.JarOutputStream
import java.util.jar.Manifest
import java.util.zip.CRC32
import java.util.zip.ZipEntry

class JarTask : AbstractTask() {
    var mainClass: String? = null

    override fun execute() {
        println("Generating Jar for ${project.name}...")

        val mainClass = mainClass ?: run {
            println("mainClass has not been setup for JarTask. Please make sure you specify the mainClass in your build file")

            return
        }

        val libsDir = File("${project.projectDir.path}/$BUILD_LIBS_DIR")
        val outputFile = File(libsDir, "/${project.name}.jar")

        if (!libsDir.exists()) {
            libsDir.mkdirs()
        }

        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }

        val fileOutputStream = outputFile.outputStream()

        val manifest = Manifest().apply {
            mainAttributes[Attributes.Name("Manifest-Version")] = "1.0"
            mainAttributes[Attributes.Name("Main-Class")] = mainClass.replace(".kt", "Kt")
        }

        val jarOutputStream = JarOutputStream(fileOutputStream, manifest)

        addProject(project, jarOutputStream)

        addDependencies(project, jarOutputStream)

        jarOutputStream.close()

        println("Jar Complete!")
    }

    private fun addDependencies(project: Project, jarOutputStream: JarOutputStream) {
        //TODO Need to test
        project.dependencyScope?.dependencies?.forEach { dependency ->
            when (dependency) {
                is Dependency.Project -> {
                    //TODO Rtaylor -
                    // When the root project is added it adds all class files form BUILD_CLASSES_DIR which would
                    // include any subproject class files. So there is no need to add subprojects here.
                    // We may one to change this because this makes assumptions about the BuildTask and if
                    // they change in the future it would break this. But if we pulled the class files from
                    // the subprojects BUILD_CLASSES_DIR we wouldn't run into this issue
                }

                is Dependency.Remote -> {
                    val location = buildLocation(dependency.value)

                    val rootFile = File("${getDependencyCacheDir()}/${location.cacheKey}/class-files/")

                    rootFile.relativeFiles { file, string ->
                        //TODO Rtaylor - Need to make a Manifest merger
                        if (!file.path.contains("MANIFEST.MF")) {
                            val relativePath = file.relativeTo(rootFile).path
                            addFile(file, relativePath, jarOutputStream)
                        }
                    }
                }
            }
        }
    }

    private fun addProject(project: Project, jarOutputStream: JarOutputStream) {
        File("${project.projectDir.path}/$BUILD_CLASSES_DIR").relativeFiles { file, relativePath ->
            addFile(file, relativePath, jarOutputStream)
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

        try {
            jar.putNextEntry(entry)
            jar.write(bytes)
            jar.closeEntry()
        } catch (e: Exception) {
//            e.printStackTrace()
        }
    }
}