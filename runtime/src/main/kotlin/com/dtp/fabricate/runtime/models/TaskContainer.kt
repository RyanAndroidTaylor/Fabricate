package com.dtp.fabricate.runtime.models

import com.dtp.fabricate.runtime.tasks.JarTask
import com.dtp.fabricate.runtime.tasks.Task

class TaskContainer {
    private val tasks: MutableMap<String, Task> = mutableMapOf()

    val jar: JarTask
        get() {
            val task = (tasks["jar"] as? JarTask) ?: JarTask(Project)

            tasks["jar"] = task

            return task
        }

    fun jar(block: JarTask.() -> Unit) {
        val task = (tasks["jar"] as? JarTask) ?: JarTask(Project)

        tasks["jar"] = task

        task.block()
    }
}