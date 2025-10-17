package com.dtp.fabricate.runtime.models

import com.dtp.fabricate.runtime.tasks.JarTask
import com.dtp.fabricate.runtime.tasks.LazyTaskProvider
import com.dtp.fabricate.runtime.tasks.Task
import com.dtp.fabricate.runtime.tasks.TaskProvider
import kotlin.reflect.KClass

class TaskContainer {

    /**
     * List containing all Registered task.
     * Registering a task does not imply that it will be created and run. For a Task to be created, configured and ran
     * it must be added to the [queuedTasks] or one of the [queuedTasks] has a dependency on it
     */
    private val taskRegistry: MutableMap<String, TaskProvider<*>> = mutableMapOf()

    /**
     * Keys of the tasks that have been explicitly queued for execution.
     */
    private val queuedTasks = mutableSetOf<String>()

    @Suppress("UNCHECKED_CAST")
    val jar: JarTask
        get() = (taskRegistry["jar"] as TaskProvider<JarTask>).task

    fun hasTask(taskName: String): Boolean = taskRegistry.contains(taskName)

    fun enqueueTask(taskName: String) {
        //TODO Should we throw error if multiple of the same tasks are added?
        queuedTasks.add(taskName)
    }

    /**
     * Locates a task by name, without triggering its creation or configuration
     *
     * [configurationAction] Run only when the Task is created and is used to configure the Task
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Task> named(taskName: String, configurationAction: T.() -> Unit) {
        taskRegistry[taskName]?.let { taskProvider ->
            (taskProvider as TaskProvider<T>).configure { it.configurationAction() }
        } ?: throw IllegalArgumentException("No Task with name $taskName found.")
    }

    /**
     * Defines a new Task that will be created when needed. To keep things simple for now only Task with empty constructors can work.
     *
     * [configurationAction] Run only when the Task is created and is used to configure the Task
     */
    fun <T : Task> register(taskName: String, taskType: KClass<T>, configurationAction: T.() -> Unit) {
        if (taskRegistry.contains(taskName)) throw IllegalArgumentException("Duplicate Tasks with name: $taskName")

        taskRegistry[taskName] = LazyTaskProvider(taskName, taskType, configurationAction)
    }

    /**
     * Registers a new Task of type T, this task will not be created or configured until it is required.
     */
    fun <T : Task> register(taskName: String, taskType: KClass<T>) {
        if (taskRegistry.contains(taskName)) throw IllegalArgumentException("Duplicate Tasks with name: $taskName")

        taskRegistry[taskName] = LazyTaskProvider(taskName, taskType)
    }

    @Suppress("UNCHECKED_CAST")
    fun jar(action: JarTask.() -> Unit) {
        (taskRegistry["jar"] as TaskProvider<JarTask>).configure(action)
    }

    fun generateTaskGraph(): List<TaskProvider<*>> {
        return queuedTasks.map {
            taskRegistry[it]!!
        }
    }
}