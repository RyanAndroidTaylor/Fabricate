package com.dtp.fabricate.runtime.deps

import com.dtp.fabricate.runtime.network.Network
import com.dtp.fabricate.runtime.tasks.AbstractTask
import java.io.File
import java.net.URI

class SyncTask : AbstractTask() {

    val network = Network()

    override fun execute() {
        println("Resolving dependencies...")

        val dependencyScope = project.dependencyScope
        val dependencies = dependencyScope?.dependencies ?: let {
            print("Build script does not have a dependencies block")

            return
        }

        val cache = DependencyCache(getDependencyCacheDir())

        dependencies.forEach {
            val location = buildUrl(it)

            val dependency = cache.find(location.cacheKey)

            if (dependency != null) {
                println("Dependency (UP TO DATE): ${location.cacheKey}")
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
                println("Complete")
            }
        }
    }

    private fun getDependencyCacheDir(): File {
        //TODO Need to figure out how to get to ~/ dir
        val root = File("/Users/ryantaylor/.fabricate/")

        if (!root.exists()) {
            root.mkdir()
        }

        return root
    }

// org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2
// segment[0] = org.jetbrains.kotlinx
// segment[1] = kotlinx-coroutines-core
// segment[2] = 1.10.2

    // https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core/1.10.2/kotlinx-coroutines-core-1.10.2.jar
//  Maven domain                        segment[0]                 segment[1]                 segment[2]    segment[1]                   segment[2]
// [https://repo1.maven.org/maven2]    [org/jetbrains/kotlinx]    [kotlinx-coroutines-core]  [1.10.2]      [kotlinx-coroutines-core]    [1.10.2]
// MavenDomain/segment[0]/segment[1]/segment[2]/segment[1]-segment[2].jar
    private fun buildUrl(dependency: String): DependencyLocation {
        val segments = dependency.split(":")
        val path = segments[0].replace('.', '/')
        val id = segments[1]
        val version = segments[2]

        return DependencyLocation(
            fileName = "$version.jar",
            cacheKey = "$path/$id/$version",
            remoteUrl = "https://repo1.maven.org/maven2/$path/$id/$version/$id-$version.jar"
        )
    }
}

private data class DependencyLocation(
    val fileName: String,
    val cacheKey: String,
    val remoteUrl: String
)
