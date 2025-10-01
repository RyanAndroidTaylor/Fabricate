package com.dtp.fabric

import java.io.File

fun main(vararg args: String) {
    when {
        args.contains("-build") -> buildProject()
        args.contains("-run") -> runProject()
        else -> println("No arguments were passed so no work was done")
    }
}

private fun runProject() {
    // TODO pull info from actual Project object
    println("Running...")
    var runnable = getProjectRunnable()

    if (runnable == null) {
        buildProject()

        runnable = getProjectRunnable()
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

private fun buildProject() {
    println("Building...")
    val processBuilder = ProcessBuilder("/bin/bash", "-c", "kotlinc Example.kt -include-runtime -d Example.jar")
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