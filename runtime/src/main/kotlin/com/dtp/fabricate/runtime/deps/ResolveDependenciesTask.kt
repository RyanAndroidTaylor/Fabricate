package com.dtp.fabricate.runtime.deps

import com.dtp.fabricate.runtime.tasks.Task
import java.io.File

class ResolveDependenciesTask(val dependencies: Set<String>) : Task {

    override fun run() {
        println("Resolving dependencies...")

        val cache = DependencyCache(getDependencyCacheDir())

        dependencies.forEach {
            val location = buildUrl(it)

            val dependency = cache.find(location.cacheKey)

            if (dependency != null) {
                println("Found Dependency ${location.cacheKey}")
            } else {
                // TODO download an return file
            }
        }

        //TODO Download all needed dependencies
    }

    private fun getDependencyCacheDir(): File {
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
            cacheKey = "$path/$id/$version",
            remoteUrl = "https://repo1.maven.org/maven2/$path/$id/$version/$id-$version.jar"
        )
    }
}

private data class DependencyLocation(
    val cacheKey: String,
    val remoteUrl: String
)
