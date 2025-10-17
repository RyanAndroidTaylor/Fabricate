package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.models.Project
import java.io.File

class RunTask(val project: Project) : AbstractTask() {
    override fun run() {
        println("Running...")
        var runnable = getProjectRunnable()

        if (runnable == null) {
            println("Failed to find runnable for ${project.name}")

            return
        }

        val processBuilder = ProcessBuilder("/bin/bash", "-c", "java -jar $runnable")
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

        println("Run Finished!")
    }

    private fun getProjectRunnable(): String? {
        val projectName = "Example"
        val projectDir = File(".")

        return projectDir.list()?.firstOrNull { it == "$projectName.jar" }
    }
}
