package com.dtp.fabricate.runtime.tasks

interface Task {
    val dependencies: List<String>

    fun execute()

    fun dependsOn(vararg taskNames: String)
}