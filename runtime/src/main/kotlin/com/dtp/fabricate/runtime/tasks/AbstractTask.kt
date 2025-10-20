package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.models.Project

abstract class AbstractTask : Task {

    override lateinit var project: Project

    override val dependencies: List<String>
        get() = mutableDependencies

    private val mutableDependencies = mutableListOf<String>()

    override fun dependsOn(vararg taskNames: String) {
        taskNames.forEach { mutableDependencies.add(it) }
    }
}