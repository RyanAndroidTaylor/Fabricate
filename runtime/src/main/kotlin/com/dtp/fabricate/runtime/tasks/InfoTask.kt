package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.models.Project

class InfoTask(val project: Project) : Task {
    override fun run() {
        println("Project: ${project.name}")
        project.dependencyScope?.let { scope ->
            println("    Dependencies:")
            scope.dependencies.forEach {
                println("        $it")
            }
        }
        println("    JarTask:")
        println("        Package: ${project.tasks.jar.mainPackage}")
        println("        MainClass: ${project.tasks.jar.mainClass}")
    }
}