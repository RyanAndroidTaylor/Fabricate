package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.BUILD_CLASSES_DIR
import com.dtp.fabricate.runtime.BUILD_LIBS_DIR
import com.dtp.fabricate.runtime.deps.buildLocation
import com.dtp.fabricate.runtime.deps.getDependencyCacheDir
import com.dtp.fabricate.runtime.models.Dependency
import com.dtp.fabricate.runtime.models.Project
import java.io.File
import java.util.jar.Attributes
import java.util.jar.JarOutputStream
import java.util.jar.Manifest
import java.util.zip.CRC32
import java.util.zip.ZipEntry

//TODO Getting closer, had to include the kotlin stdlib and coroutines and now I'm getting this error.
// I'm guessing the coroutine library and kotlin-stdlib will have some duplicate stuff. If they are
// compatible versions is it alright to just take one and skip the other?
/*
Exception in thread "main" java.util.zip.ZipException: duplicate entry: META-INF/versions/9/module-info.class
	at java.base/java.util.zip.ZipOutputStream.putNextEntry(ZipOutputStream.java:245)
	at java.base/java.util.jar.JarOutputStream.putNextEntry(JarOutputStream.java:115)
	at com.dtp.fabricate.runtime.tasks.JarTask.addFile(JarTask.kt:132)
	at com.dtp.fabricate.runtime.tasks.JarTask.addDependencies(JarTask.kt:91)
	at com.dtp.fabricate.runtime.tasks.JarTask.execute(JarTask.kt:57)
	at com.dtp.fabricate.runtime.daemon.SimpleDaemon.runTask(SimpleDaemon.kt:34)
	at com.dtp.fabricate.runtime.daemon.SimpleDaemon.executeCommands(SimpleDaemon.kt:19)
	at com.dtp.fabricate.MainKt.main(Main.kt:39)
	 */
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

        val fileOutputStream = outputFile.outputStream()

        val manifest = Manifest().apply {
            mainAttributes[Attributes.Name("Manifest-Version")] = "1.0"
            mainAttributes[Attributes.Name("Main-Class")] = mainClass.replace(".kt", "Kt")
        }

        val jarOutputStream = JarOutputStream(fileOutputStream, manifest)

        addProject(project, jarOutputStream)

        //TODO Need to add kotlin_module files (not sure how to do this)
        addDependencies(project, jarOutputStream)

        jarOutputStream.close()

        println("Jar Complete!")
    }

    private fun addDependencies(project: Project, jarOutputStream: JarOutputStream) {
        //TODO Need to test
        project.dependencyScope?.dependencies?.forEach { dependency ->
            when (dependency) {
                is Dependency.Project -> {
                    //TODO Maybe?
//                    addProject(project, jarOutputStream)
                }

                is Dependency.Remote -> {
                    val location = buildLocation(dependency.value)

                    val rootFile = File("${getDependencyCacheDir()}/${location.cacheKey}/class-files/")

                    val dirs = mutableListOf(rootFile)

                    while (dirs.isNotEmpty()) {
                        val current = dirs.removeAt(0)

                        current.listFiles()?.forEach { file ->
                            if (file.isDirectory) {
                                dirs.add(file)
                            } else {
                                //TODO This is not great. Probably should combine the manifest files into a single file
                                // Most of them won't have any unique info but there is a chance one will.
                                // Guess I should also do a little research to make sure combining them is what should
                                // be done.
                                if (!file.path.contains("MANIFEST.MF")) {
                                    val relativePath = file.relativeTo(rootFile).path
                                    addFile(file, relativePath, jarOutputStream)
                                }
                            }
                        }
                    }
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

        try {
            jar.putNextEntry(entry)
            jar.write(bytes)
            jar.closeEntry()
        } catch (e: Exception) {
//            e.printStackTrace()
        }
    }
}