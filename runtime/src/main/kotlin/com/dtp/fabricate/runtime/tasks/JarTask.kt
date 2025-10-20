package com.dtp.fabricate.runtime.tasks

//TODO Need to pull dependencies from cache. Which means this task will depend on SyncTask to make sure dependencies are
// downloaded.
class JarTask : AbstractTask() {
    var mainClass: String? = null

    override fun execute() {
        println("Generating Jar for ${project.name}...")

        val commandBuilder = StringBuilder()

//        TODO("This Task will depend on BuildTask and use the build folder outputted by BuildTask to generate a Jar")

//        val processBuilder = ProcessBuilder("/bin/bash", "-c", commandBuilder.toString())
//            .redirectOutput(ProcessBuilder.Redirect.PIPE)
//            .redirectError(ProcessBuilder.Redirect.PIPE)
//            .start()
//
//        processBuilder.waitFor()
//
//        val output = processBuilder.inputStream.bufferedReader().readText()
//        val error = processBuilder.errorStream.bufferedReader().readText()
//
//        if (output.isNotBlank()) {
//            println(output)
//        }
//        if (error.isNotBlank()) {
//            println(error)
//        }
//
//        println("Build Complete!")
    }

}