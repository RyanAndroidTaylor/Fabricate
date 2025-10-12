package com.dtp.fabricate.runtime.models

object Project {
    var name: String = "abc"

    var dependencyScope: DependencyScope? = null
        private set

    private val taskContainer = TaskContainer()

    val tasks: TaskContainer
        get() = taskContainer

    fun dependencies(block: DependencyScope.() -> Unit) {
        dependencyScope = DependencyScope().apply {
            block()
        }
    }
}