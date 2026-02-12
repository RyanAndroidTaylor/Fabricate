package com.dtp.fabricate.runtime.deps

import com.dtp.fabricate.runtime.models.Dependency
import com.dtp.fabricate.runtime.network.Network
import com.dtp.fabricate.runtime.tasks.AbstractTask
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.util.concurrent.TimeUnit
import java.util.jar.JarInputStream

/**
 * SyncTask downloads and caches all dependencies of type Dependency.Remote, skipping and dependencies that have
 * already been cached.
 */
class SyncTask : AbstractTask() {

    val network = Network()
    val cache = DependencyCache(getDependencyCacheDir())

    override fun execute() {
        println("Resolving dependencies...")

        project.forEachProject {
            it.dependencyScope?.dependencies?.filterIsInstance<Dependency.Remote>()?.forEach { dependency ->
                downloadDependency(dependency)
            }
        }
    }

    /**
     * Downloads the dependency jar then extracts the class file caching everything in the Fabricate
     * cache directory (see [DependencyLocation]).
     */
    private fun downloadDependency(dependency: Dependency.Remote) {
        val location = buildLocation(dependency.value)

        val dependency = cache.find(location.cacheKey)

        if (dependency != null) {
            println("(UP TO DATE): ${location.cacheKey}")
        } else {
            println("Downloading: ${getDependencyCacheDir()}/${location.cacheKey}/${location.fileName}")

            val bytes = network.download(URI(location.remoteUrl).toURL())

            val directory = File("${getDependencyCacheDir()}/${location.cacheKey}/")
            val file = File(directory, location.fileName)

            if (!directory.exists()) {
                directory.mkdirs()
            }

            if (!file.exists()) {
                file.createNewFile()
            }

            file.writeBytes(bytes)

            extractClassFiles(location)

            println("Complete")
        }
    }

    private fun extractClassFiles(location: DependencyLocation) {
        println("Extracting Class Files...")

        //TODO WIP: Extract deps using JarInputStream instead of unzip command
//        val fileInputStream = File("${getDependencyCacheDir()}/${location.cacheKey}/${location.fileName}").inputStream()
//        val fileOutput = File("${getDependencyCacheDir()}/${location.cacheKey}/class-files/")
//
//        val jarInputStream = JarInputStream(fileInputStream)
//
//        val manifest = jarInputStream.manifest
//        val jarEntry = jarInputStream.nextJarEntry


        // TODO Remove once new version is done
        oldExtractFiles(location)

        println("Class Files Extracted")
    }

    private fun oldExtractFiles(location: DependencyLocation) {
        val commandBuilder = StringBuilder().apply {
            append("unzip")
            append(" -o")
            append(" ${getDependencyCacheDir()}/${location.cacheKey}/${location.fileName}")
            append(" -d")
            append(" ${getDependencyCacheDir()}/${location.cacheKey}/class-files/")
        }

        println("Running command: $commandBuilder")

        val processBuilder = ProcessBuilder("/bin/bash", "-c", commandBuilder.toString())
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        processBuilder.waitFor(5_000L, TimeUnit.MILLISECONDS)

        println("Command Complete")

        val outputReader = processBuilder.inputStream.bufferedReader()
        val output = outputReader.readText()
        val errorReader = processBuilder.errorStream.bufferedReader()
        val error = errorReader.readText()

        if (output.isNotBlank()) {
            println(output)
        }
        if (error.isNotBlank()) {
            println(error)
        }

        outputReader.close()
        errorReader.close()
    }
}