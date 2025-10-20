package com.dtp.fabricate.runtime.models

import java.io.File

class Settings {
    var projectName: String = ""
        set(value) {
            rootProject = Project(value, File("./"))

            field = value
        }

    lateinit var rootProject: Project
        private set

    val subProjects = mutableMapOf<String, Project>()

    fun include(module: String) {
        subProjects[module] = Project(module, File("./$module"))
    }

    fun project(name: String?): Project {
        return name?.let { subProjects[it]!! } ?: rootProject
    }
}