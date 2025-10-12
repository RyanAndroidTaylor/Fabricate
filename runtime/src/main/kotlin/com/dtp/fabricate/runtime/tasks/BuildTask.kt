package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.models.Project

//TODO Need to pull dependencies from cache. Which means this task will depend on SyncTask to make sure dependencies are
// downloaded.
class BuildTask(val project: Project) : Task {
    override fun run() {
        println("Building ${project.name}...")

        val processBuilder = ProcessBuilder("/bin/bash", "-c", "kotlinc Example.kt -d Example.jar")
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
}