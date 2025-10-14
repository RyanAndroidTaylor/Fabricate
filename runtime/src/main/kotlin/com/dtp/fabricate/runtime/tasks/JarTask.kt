package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.KOTLIN_SRC_DIR
import com.dtp.fabricate.runtime.models.Project
import java.io.File

//TODO Need to pull dependencies from cache. Which means this task will depend on SyncTask to make sure dependencies are
// downloaded.
class JarTask(val project: Project) : Task {
    var mainPackage: String = ""

    var mainClass: String? = null

    override fun run() {
        println("Building ${project.name}...")

        val commandBuilder = StringBuilder()

        with(commandBuilder) {
            append("kotlinc")
            // KT file with main function
            append(" $KOTLIN_SRC_DIR/${mainPackage.replace('.', '/')}/$mainClass")
            // Output file
            append(" -d ${mainClass?.replace(".kt", ".jar")}")
            // All src files
            collectSrcFiles().forEach {
                append(" $it")
            }
        }

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

        val root = File("$KOTLIN_SRC_DIR/${mainPackage.replace('.', '/')}")

        val fileIterator = root.walkTopDown().iterator()

        while (fileIterator.hasNext()) {
            val next = fileIterator.next()

            if (next.isFile && next.name != mainClass) {
                srcFiles.add(next.path)
            }
        }

        return srcFiles
    }
}