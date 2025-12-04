package com.dtp.fabricate.runtime.deps

import java.io.File

/**
 *                               com
 *                             /    \
 *                           /       \
 *                         /          \
 *                       /             \
 *                     /                \
 *                 abc                  xyz
 *            /    |      \              |
 *    service    One.kt    repo         Six.kt
 *    /     \               |
 * Two.kt Three.kt        Four.kt
 *
 *
 * com             []
 * com-abc         [One.kt]
 * com-abc-service [Two.kt, Three.kt]
 * com-abc-service [Four.kt]
 * com-xyz         [Six.kt]
 *
 *
 */
class DependencyCache(val root: File) {
    fun find(cacheKey: String): File? {
        val dependency = File(root, cacheKey)

        return if (dependency.exists()) {
            dependency
        } else {
            null
        }
    }
}

fun getDependencyCacheDir(): File {
    //TODO Need to figure out how to get to ~/ dir
    val root = File("/Users/ryantaylor/.fabricate/")

    if (!root.exists()) {
        root.mkdir()
    }

    return root
}
