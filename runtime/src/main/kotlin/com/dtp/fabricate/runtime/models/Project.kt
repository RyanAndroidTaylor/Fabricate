package com.dtp.fabricate.runtime.models

object Project {
    lateinit var name: String
    lateinit var projectPackage: String

    val packageAsDir: String
        get() = projectPackage.replace('.', '/')

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