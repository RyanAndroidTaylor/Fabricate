package com.dtp.fabricate.runtime.models

import com.dtp.fabricate.runtime.tasks.Task
import java.io.File

class Project(
    val name: String,
    val projectDir: File,
    val isRoot: Boolean = false,
    val children: List<Project> = emptyList(),
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

    fun subProject(name: String?): Project? {
        return when (name) {
            this.name -> this
            else -> name?.let { children.firstOrNull { it.name == name } }
        }
    }

    /**
     * Loops over the entire project tree starting with this project calling [block]
     */
    inline fun forEachProject(block :(Project) -> Unit) {
        val projects = mutableListOf(this)

        while (projects.isNotEmpty()) {
            val current = projects.removeLast()

            block(current)

            // Add all module dependencies to the projects list
            current.dependencyScope?.dependencies?.filterIsInstance<Dependency.Project>()?.forEach { dependency ->
                subProject(dependency.module)?.let { projects.add(it) }
            }
        }
    }

    fun buildGraph(rootTask: String): List<String> {
        val graph = mutableListOf<MutableSet<String>>(mutableSetOf())
        val layers = mutableListOf(listOf(rootTask))

        var layer = 0

        while (layer < layers.size) {
            val current = layers[layer]
            val nextLayer = mutableListOf<String>()

            current.forEach { taskName ->
                // This will trigger configuration of task if this is the fist time it is accessed
                val task = this.tasks.named<Task>(taskName).task

                graph[layer].add(taskName)

                for (i in (layer - 1) downTo 0) {
                    graph[i].remove(taskName)
                }

                nextLayer.addAll(task.dependencies)
            }

            if (nextLayer.isNotEmpty()) {
                layers.add(nextLayer)
                graph.add(mutableSetOf())
            }

            layer++
        }

        return graph.flatMap { it }
            .reversed()
    }
}