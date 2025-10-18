package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.models.TaskContainer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SimpleTask : AbstractTask() {
    override fun execute() { /* no-op for tests */ }
}

class TaskGraphTest {

    @Test
    fun `single task no dependencies returns only root`() {
        val container = TaskContainer()
        container.register("root", SimpleTask::class)

        val graph = TaskGraph(container).buildGraph("root")

        assertEquals(listOf("root"), graph)
    }

    @Test
    fun `linear chain dependencies before root`() {
        val container = TaskContainer()
        container.register("c", SimpleTask::class)
        container.register("b", SimpleTask::class) { dependsOn("c") }
        container.register("a", SimpleTask::class) { dependsOn("b") }

        val order = TaskGraph(container).buildGraph("a")

        // Expect deepest dependency first, then up to root
        assertEquals(listOf("c", "b", "a"), order)
    }

    @Test
    fun `diamond graph shared dependency is deduplicated and first`() {
        val container = TaskContainer()
        container.register("d", SimpleTask::class)
        container.register("b", SimpleTask::class) { dependsOn("d") }
        container.register("c", SimpleTask::class) { dependsOn("d") }
        container.register("a", SimpleTask::class) { dependsOn("b", "c") }

        val order = TaskGraph(container).buildGraph("a")

        // d must execute before b and c; a last. d should only appear once
        assertEquals("d", order.first(), "Shared dependency should be first")
        assertEquals("a", order.last(), "Root should be last")
        // Middle two can be in any order but must contain exactly b and c
        val middle = order.subList(1, 3).toSet()
        assertEquals(setOf("b", "c"), middle)
    }

    @Test
    fun `duplicate dependencies deduplicated in result`() {
        val container = TaskContainer()
        container.register("x", SimpleTask::class)
        container.register("a", SimpleTask::class) { dependsOn("x", "x") }

        val order = TaskGraph(container).buildGraph("a")

        // Even if declared twice, x should only be scheduled once
        assertEquals(listOf("x", "a"), order)
    }

    @Test
    fun `missing root task throws`() {
        val container = TaskContainer()
        // Do not register root
        assertFailsWith<NullPointerException> {
            TaskGraph(container).buildGraph("missing")
        }
    }

    @Test
    fun `missing dependency throws`() {
        val container = TaskContainer()
        container.register("a", SimpleTask::class) { dependsOn("notRegistered") }

        assertFailsWith<NullPointerException> {
            TaskGraph(container).buildGraph("a")
        }
    }
}
