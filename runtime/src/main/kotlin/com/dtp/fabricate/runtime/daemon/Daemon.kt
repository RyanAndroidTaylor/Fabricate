package com.dtp.fabricate.runtime.daemon

import com.dtp.fabricate.runtime.models.TaskContainer

interface Daemon {
    fun runTasks(
        taskNames: List<String>,
        taskContainer: TaskContainer
    )
}