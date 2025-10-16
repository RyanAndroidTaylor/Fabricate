package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.Action

interface TaskProvider <T: Task> {
    val name: String

    /**
     * Gets the underlying Task. If the task has not been created it will be created and configured before returning
     */
    val task: T

    /**
     * Adds an action to be run when the underlying Task is configured. This does not trigger configuration
     */
    fun configure(configAction: Action<T>)
}