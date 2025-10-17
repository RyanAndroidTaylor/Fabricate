package com.dtp.fabricate.runtime.tasks

abstract class AbstractTask : Task {
    override val dependencies: List<String>
        get() = mutableDependencies

    private val mutableDependencies = mutableListOf<String>()

    override fun dependsOn(vararg taskNames: String) {
        taskNames.forEach { mutableDependencies.add(it) }
    }
}