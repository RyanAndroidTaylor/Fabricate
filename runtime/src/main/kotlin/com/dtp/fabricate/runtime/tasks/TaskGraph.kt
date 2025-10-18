package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.models.TaskContainer

class TaskGraph(val taskContainer: TaskContainer) {
    fun buildGraph(rootTask: String): List<String> {
        val graph = mutableListOf<MutableSet<String>>(mutableSetOf())
        val layers = mutableListOf(mutableListOf(rootTask))

        var layer = 0

        while (layer < layers.size) {
            val current = layers[layer]
            val nextLayer = mutableListOf<String>()

            current.forEach { taskName ->
                // This will trigger configuration of task if this is the fist time it is accessed
                val task = taskContainer.named<Task>(taskName).task

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
