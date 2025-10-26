package com.dtp.fabricate.runtime.models

class DependencyScope {
    val dependencies = mutableSetOf<Dependency>()

    fun implementation(dependency: String) {
        dependencies.add(Dependency.Remote(dependency))
    }

    fun implementation(projectDependency: Dependency.Project) {
        dependencies.add(projectDependency)
    }

    fun project(module: String): Dependency.Project = Dependency.Project(module)
}

sealed interface Dependency {
    data class Remote(val value: String) : Dependency
    data class Project(val module: String) : Dependency
}