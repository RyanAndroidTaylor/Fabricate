package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.BUILD_CLASSES_DIR
import com.dtp.fabricate.runtime.KOTLIN_SRC_DIR
import com.dtp.fabricate.runtime.models.Dependency
import com.dtp.fabricate.runtime.models.Project
import java.io.File

/**
 * Builds the given project and all of its children. Building generates all the class files for the project as well as
 * all child projects it depends on. It stores these class files in the "project.projectDir"/build/classes dir. It also
 * pulls in all the class files from any external dependencies (these should be downloaded and cached during the SyncTask
 * which this task depends on). Once complete the "project.projectDir"/build/classes dir should contain all the class
 * files needed to generate a Jar.
 */
class BuildTask : AbstractTask() {
    override fun execute() {
        println("Building...")

        project.forEachProject { buildProject(it) }

        println("Build Complete!")
    }

    private fun buildProject(project: Project) {
        val commandBuilder = StringBuilder()

        with(commandBuilder) {
            append("kotlinc -d ${project.projectDir.path}/$BUILD_CLASSES_DIR")

            // Root project src
            append(" ${project.projectDir}/$KOTLIN_SRC_DIR")

            // Only build the children this project depends on
            project.dependencyScope?.dependencies?.filterIsInstance<Dependency.Project>()?.forEach { dependency ->
                project.children.firstOrNull { it.name == dependency.module }?.let {
                    append(" ${it.projectDir}/$KOTLIN_SRC_DIR")
                }
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

        println("(BUILT): ${project.name}")
    }
}