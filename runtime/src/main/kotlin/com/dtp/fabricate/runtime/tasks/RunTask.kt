package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.BUILD_LIBS_DIR
import java.io.File

class RunTask : AbstractTask() {
    override fun execute() {
        println("Running...\n")

        val runnable = getProjectRunnable()

        if (runnable == null) {
            println("Failed to find runnable for ${project.name}")

            return
        }

        val processBuilder = ProcessBuilder("/bin/bash", "-c", "java -jar ${project.projectDir.path}/${BUILD_LIBS_DIR}/$runnable")
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
        val projectDir = File("${project.projectDir.path}/$BUILD_LIBS_DIR")

        return projectDir.list()?.firstOrNull {
           it == "${project.name}.jar"
        }
    }
}
