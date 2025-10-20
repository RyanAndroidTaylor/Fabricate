package com.dtp.fabricate.runtime.models

import java.io.File

class Project(
    val name: String,
    val projectDir: File,
) {
    private val taskContainer: TaskContainer = TaskContainer(this)

    var dependencyScope: DependencyScope? = null
        private set

    val tasks: TaskContainer
        get() = taskContainer

    fun dependencies(block: DependencyScope.() -> Unit) {
        dependencyScope = DependencyScope().apply {
            block()
        }
    }
}