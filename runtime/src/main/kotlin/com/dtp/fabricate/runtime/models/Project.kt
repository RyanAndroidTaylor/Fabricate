package com.dtp.fabricate.runtime.models

object Project {
    var name: String = "abc"

    var dependencyScope: DependencyScope? = null
        private set


    fun dependencies(block: DependencyScope.() -> Unit) {
        dependencyScope = DependencyScope().apply {
            block()
        }
    }
}