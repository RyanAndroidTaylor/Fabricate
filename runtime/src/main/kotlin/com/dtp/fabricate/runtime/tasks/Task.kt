package com.dtp.fabricate.runtime.tasks

interface Task {
    val dependencies: List<String>

    fun run()

    fun dependsOn(vararg taskNames: String)
}