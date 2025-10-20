package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.BUILD_CLASSES_DIR
import com.dtp.fabricate.runtime.KOTLIN_SRC_DIR
import java.io.File

class BuildTask : AbstractTask() {
    override fun execute() {
        println("Building ${project.name}")

        val commandBuilder = StringBuilder()

        with (commandBuilder) {
            append("kotlinc -d ${project.projectDir.path}/$BUILD_CLASSES_DIR")

            collectSrcFiles().forEach {
                append(" $it")
            }

            // This controls the name of the META-INF/*.kotlin_module
            // This file hold all the information about top level members which helps compilation times.
            // We need to make sure this is uniquely named for each module when building a multi module project
            // See: https://blog.jetbrains.com/kotlin/2015/06/improving-java-interop-top-level-functions-and-properties/
            append(" -module-name ${project.name}")
        }

        println("Compiling with: $commandBuilder")

        val processBuilder = ProcessBuilder("/bin/bash", "-c", commandBuilder.toString())
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        processBuilder.waitFor()

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

        println("Build Complete!")
    }

    private fun collectSrcFiles(): List<String> {
        val srcFiles = mutableListOf<String>()

        val root = File("${project.projectDir.path}/$KOTLIN_SRC_DIR")

        if (!root.exists()) {
            throw IllegalStateException("No src dir found at ${root.absoluteFile}")
        }

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