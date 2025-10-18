package com.dtp.fabricate.runtime.daemon

import com.dtp.fabricate.runtime.models.TaskContainer
import com.dtp.fabricate.runtime.tasks.Task
import com.dtp.fabricate.runtime.tasks.TaskGraph

/**
 * Currently this is not a real daemon, but it is split out in a way that it
 * could be converted in the future
 */
class Daemon {
    fun runTask(
        taskName: String,
        taskContainer: TaskContainer
    ) {
        TaskGraph(taskContainer).buildGraph(taskName).forEach { taskName ->
            taskContainer.named<Task>(taskName).task.execute()
        }
    }
}
