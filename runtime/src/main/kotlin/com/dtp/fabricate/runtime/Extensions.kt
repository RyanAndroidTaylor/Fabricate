package com.dtp.fabricate.runtime

import java.io.File

/**
 * Finds all files in this directory and all subdirectories and calls [block]
 * with the path relative to [this]
 *
 * Example: [block] would be called once with the path /text_file.text and once
 * with the path /sub/more_text.txt
 *  - Root = Home/documents
 *  - Child = Home/documents/text_file.txt
 *  - Child = Home/documents/sub/more_text.txt
 */
inline fun File.relativeFiles(block: (File, String) -> Unit) {
    val dirs = mutableListOf(this)

    while (dirs.isNotEmpty()) {
        val current = dirs.removeAt(dirs.lastIndex)

        current.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                dirs.add(file)
            } else {
                block(file, file.relativeTo(this).path)
            }
        }
    }
}