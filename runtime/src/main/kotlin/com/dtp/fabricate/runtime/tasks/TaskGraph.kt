package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.models.Project

class TaskGraph(val project: Project) {
    fun buildGraph(rootTask: String): List<String> {
        val graph = mutableListOf<MutableSet<String>>(mutableSetOf())
        val layers = mutableListOf(listOf(rootTask))

        var layer = 0

        while (layer < layers.size) {
            val current = layers[layer]
            val nextLayer = mutableListOf<String>()

            current.forEach { taskName ->
                // This will trigger configuration of task if this is the fist time it is accessed
                val task = project.tasks.named<Task>(taskName).task

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
