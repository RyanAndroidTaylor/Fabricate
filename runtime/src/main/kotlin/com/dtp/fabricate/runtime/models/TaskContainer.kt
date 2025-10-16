package com.dtp.fabricate.runtime.models

import com.dtp.fabricate.runtime.Action
import com.dtp.fabricate.runtime.tasks.JarTask
import com.dtp.fabricate.runtime.tasks.LazyTaskProvider
import com.dtp.fabricate.runtime.tasks.Task
import com.dtp.fabricate.runtime.tasks.TaskProvider
import kotlin.reflect.KClass

class TaskContainer {

    private val tasks: MutableMap<String, Task> = mutableMapOf()

    private val taskRegistry: MutableMap<String, TaskProvider<*>> = mutableMapOf()

    val jar: JarTask
        get() {
            val task = (tasks["jar"] as? JarTask) ?: JarTask(Project)

            tasks["jar"] = task

            return task
        }

    /**
     * Locates a task by name, without triggering its creation or configuration
     *
     * [configurationAction] Run only when the Task is created and is used to configure the Task
     */
    fun <T: Task> named(taskName: String, configurationAction: Action<T>) {
        taskRegistry[taskName]?.let { taskProvider ->
            (taskProvider as TaskProvider<T>).configure(configurationAction)
        } ?: throw IllegalArgumentException("No Task with name $taskName found.")
    }

    /**
     * Defines a new Task that will be created when needed. To keep things simple for now only Task with empty constructors can work.
     *
     * [configurationAction] Run only when the Task is created and is used to configure the Task
     */
    fun <T: Task> register(taskName: String, taskType: KClass<T>, configurationAction: T.() -> Unit) {
        if (taskRegistry.contains(taskName)) throw IllegalArgumentException("Duplicate Tasks with name: $taskName")

        taskRegistry[taskName] = LazyTaskProvider(taskName, taskType, configurationAction)
    }

    /**
     * Registers a new Task of type T, this task will not be created or configured until it is required.
     */
    fun <T: Task> register(taskName: String, taskType: KClass<T>) {
        if (taskRegistry.contains(taskName)) throw IllegalArgumentException("Duplicate Tasks with name: $taskName")

        taskRegistry[taskName] = LazyTaskProvider(taskName, taskType)
    }

    fun jar(block: JarTask.() -> Unit) {
        val task = (tasks["jar"] as? JarTask) ?: JarTask(Project)

        tasks["jar"] = task

        task.block()
    }
}