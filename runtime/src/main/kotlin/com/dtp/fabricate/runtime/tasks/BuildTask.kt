package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.BUILD_CLASSES_DIR
import com.dtp.fabricate.runtime.KOTLIN_SRC_DIR
import com.dtp.fabricate.runtime.models.Project
import java.io.File

class BuildTask : AbstractTask() {
    override fun execute() {
        println("Building ${Project.name}")

        val commandBuilder = StringBuilder()

        with (commandBuilder) {
            append("kotlinc -d $BUILD_CLASSES_DIR")

            collectSrcFiles().forEach {
                append(" $it")
            }
        }

        println("Compiling with: $commandBuilder")

        val processBuilder = ProcessBuilder("/bin/bash", "-c", commandBuilder.toString())
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        processBuilder.waitFor()

        val output = processBuilder.inputStream.bufferedReader().readText()
        val error = processBuilder.errorStream.bufferedReader().readText()

        if (output.isNotBlank()) {
            println(output)
        }
        if (error.isNotBlank()) {
            println(error)
        }

        println("Build Complete!")
    }

    private fun collectSrcFiles(): List<String> {
        val srcFiles = mutableListOf<String>()

        val root = File(KOTLIN_SRC_DIR)

        val fileIterator = root.walkTopDown().iterator()

        while (fileIterator.hasNext()) {
            val next = fileIterator.next()

            if (next.isFile) {
                srcFiles.add(next.path)
            }
        }

        return srcFiles
    }
}