package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.models.Project

class InfoTask : AbstractTask() {
    override fun execute() {
        val project = Project

        println("Project: ${project.name}")
        project.dependencyScope?.let { scope ->
            println("    Dependencies:")
            scope.dependencies.forEach {
                println("        $it")
            }
        }
        println("    JarTask:")
        println("        Package: ${project.projectPackage}")
        println("        MainClass: ${project.tasks.jar.mainClass}")
    }
}