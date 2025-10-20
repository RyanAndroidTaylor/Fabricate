package com.dtp.fabricate.runtime.tasks


class InfoTask : AbstractTask() {
    //TODO Seems like we are not setting up the sub-projects correctly. This still prints the root project name when running is sub-project
    override fun execute() {
        println("Project: ${project.name}")

        project.dependencyScope?.let { scope ->
            println("    Dependencies:")
            scope.dependencies.forEach {
                println("        $it")
            }
        }
        println("    JarTask:")
        println("        MainClass: ${project.tasks.jar.mainClass}")
    }
}