package com.dtp.fabricate.runtime.daemon

import com.dtp.fabricate.runtime.models.TaskContainer
import com.dtp.fabricate.runtime.tasks.Task
import com.dtp.fabricate.runtime.tasks.TaskGraph

/**
 * Currently this is not a real daemon, but it is split out in a way that it
 * could be converted in the future
 */
class SimpleDaemon : Daemon {
    override fun runTasks(
        taskNames: List<String>,
        taskContainer: TaskContainer
    ) {
        TaskGraph(taskContainer).buildGraph(taskNames).forEach { taskName ->
            taskContainer.named<Task>(taskName).task.execute()
        }
    }
}
