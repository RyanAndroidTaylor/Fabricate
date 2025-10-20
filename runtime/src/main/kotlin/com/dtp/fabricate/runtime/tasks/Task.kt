package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.models.Project

interface Task {
    var project: Project

    val dependencies: List<String>

    fun execute()

    fun dependsOn(vararg taskNames: String)
}