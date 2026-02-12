package com.dtp.fabricate.runtime.deps


data class DependencyLocation(
    val fileName: String,
    val cacheKey: String,
    val remoteUrl: String
)

// org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2
// segment[0] = org.jetbrains.kotlinx
// segment[1] = kotlinx-coroutines-core
// segment[2] = 1.10.2

// https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core/1.10.2/kotlinx-coroutines-core-1.10.2.jar
//  Maven domain                        segment[0]                 segment[1]                 segment[2]    segment[1]                   segment[2]
// [https://repo1.maven.org/maven2]    [org/jetbrains/kotlinx]    [kotlinx-coroutines-core]  [1.10.2]      [kotlinx-coroutines-core]    [1.10.2]
// MavenDomain/segment[0]/segment[1]/segment[2]/segment[1]-segment[2].jar
/**
 * Creates a [DependencyLocation] that holds information about the location in caches as well as the remote URL for the
 * dependencies. Dependencies are caches at "user dir"/.fabricate/"dependency group"/"dependency name"/"version"/
 */
fun buildLocation(dependency: String): DependencyLocation {
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
