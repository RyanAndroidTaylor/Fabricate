package com.dtp.fabricate.runtime.daemon

import com.dtp.fabricate.runtime.cli.CliCommand
import com.dtp.fabricate.runtime.models.Project
import com.dtp.fabricate.runtime.tasks.Task

/**
 * Currently this is not a real daemon, but it is split out in a way that it
 * could be converted in the future
 */
class SimpleDaemon : Daemon {
    override fun executeCommands(
        commands: List<CliCommand>,
        rootProject: Project,
    ) {
        commands.forEach { (projectName, command) ->
            // No module was specified so build entire project and all submodules
            if (projectName == null) {
                runTask(command, rootProject)
            } else {
                rootProject.subProject(projectName)?.let { project ->
                    println("Running command in ${project.name}")

                    project.buildGraph(command).forEach { taskName ->
                        project.tasks.named<Task>(taskName).task.execute()
                    }
                } ?: println("Unable to find project with name $projectName")
            }
        }
    }

    private fun runTask(initialTask: String, project: Project) {
        project.buildGraph(initialTask).forEach { taskName ->
            project.tasks.named<Task>(taskName).task.execute()
        }
    }
}
