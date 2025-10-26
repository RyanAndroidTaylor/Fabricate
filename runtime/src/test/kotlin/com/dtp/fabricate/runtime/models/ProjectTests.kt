package com.dtp.fabricate.runtime.models

import com.dtp.fabricate.runtime.tasks.AbstractTask
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SimpleTask : AbstractTask() {
    override fun execute() { /* no-op for tests */ }
}

class ProjectTests {

    @Test
    fun `single task no dependencies returns only root`() {
        val project = Project("test", File(""))

        project.tasks.register("root", SimpleTask::class)

        val graph = project.buildGraph("root")

        assertEquals(listOf("root"), graph)
    }

    @Test
    fun `linear chain dependencies before root`() {
        val project = Project("test", File(""))

        with(project.tasks) {
            register("c", SimpleTask::class)
            register("b", SimpleTask::class) { dependsOn("c") }
            register("a", SimpleTask::class) { dependsOn("b") }
        }

        val order = project.buildGraph("a")

        // Expect deepest dependency first, then up to root
        assertEquals(listOf("c", "b", "a"), order)
    }

    @Test
    fun `diamond graph shared dependency is deduplicated and first`() {
        val project = Project("test", File(""))

        with (project.tasks) {
            register("d", SimpleTask::class)
            register("b", SimpleTask::class) { dependsOn("d") }
            register("c", SimpleTask::class) { dependsOn("d") }
            register("a", SimpleTask::class) { dependsOn("b", "c") }
        }

        val order = project.buildGraph("a")

        // d must execute before b and c; a last. d should only appear once
        assertEquals("d", order.first(), "Shared dependency should be first")
        assertEquals("a", order.last(), "Root should be last")
        // Middle two can be in any order but must contain exactly b and c
        val middle = order.subList(1, 3).toSet()
        assertEquals(setOf("b", "c"), middle)
    }

    @Test
    fun `duplicate dependencies deduplicated in result`() {
        val project = Project("test", File(""))

        with (project.tasks) {
            register("x", SimpleTask::class)
            register("a", SimpleTask::class) { dependsOn("x", "x") }
        }

        val order = project.buildGraph("a")

        // Even if declared twice, x should only be scheduled once
        assertEquals(listOf("x", "a"), order)
    }

    @Test
    fun `missing root task throws`() {
        val project = Project("test", File(""))

        // Do not register root
        assertFailsWith<NullPointerException> {
            project.buildGraph("missing")
        }
    }

    @Test
    fun `missing dependency throws`() {
        val project = Project("test", File(""))

        with (project.tasks) {
            register("a", SimpleTask::class) { dependsOn("notRegistered") }
        }

        assertFailsWith<NullPointerException> {
            project.buildGraph("a")
        }
    }
}
