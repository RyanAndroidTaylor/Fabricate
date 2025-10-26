package com.dtp.fabricate.runtime.models

import java.io.File

class Settings {
    private val subProjects = mutableMapOf<String, Project>()

    var projectName: String = ""

    val rootProject: Project by lazy {
        Project(
            name = projectName,
            projectDir = File("./"),
            isRoot = true,
            children = subProjects.values.toList(),
        )
    }

    fun include(module: String) {
        subProjects[module] = Project(
            name = module,
            projectDir = File("./$module"),
        )
    }
}