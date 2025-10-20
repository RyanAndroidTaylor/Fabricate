package com.dtp.fabricate.runtime.daemon

import com.dtp.fabricate.runtime.cli.CliCommand
import com.dtp.fabricate.runtime.models.Project
import com.dtp.fabricate.runtime.models.Settings
import com.dtp.fabricate.runtime.tasks.Task
import com.dtp.fabricate.runtime.tasks.TaskGraph

/**
 * Currently this is not a real daemon, but it is split out in a way that it
 * could be converted in the future
 */
class SimpleDaemon : Daemon {
    override fun executeCommands(
        commands: List<CliCommand>,
        settings: Settings,
    ) {
        commands.forEach { (projectName, command) ->
            // No module was specified so build entire project and all submodules
            if (projectName == null) {
                //TODO The root project might depend on submodules and submodules might depend on others
                // This will need to be handled similar to Tasks to make sure they are built in the right order
                runTask(command, settings.rootProject)

                settings.subProjects.values.forEach { runTask(command, it)}
            } else {
                val project = settings.project(projectName)

                println("Running command in ${project.name}")

                TaskGraph(project).buildGraph(command).forEach { taskName ->
                    project.tasks.named<Task>(taskName).task.execute()
                }
            }
        }
    }

    private fun runTask(initialTask: String, project: Project) {
        TaskGraph(project).buildGraph(initialTask).forEach { taskName ->
            project.tasks.named<Task>(taskName).task.execute()
        }
    }
}
