package com.dtp.fabricate.runtime.daemon

import com.dtp.fabricate.runtime.cli.CliCommand
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
            val project = settings.project(projectName)

            println("Running command in ${project.name}")

            TaskGraph(project).buildGraph(command).forEach { taskName ->
                project.tasks.named<Task>(taskName).task.execute()
            }
        }
    }
}
