package com.dtp.fabricate.runtime.models

class DependencyScope {
    val dependencies = mutableSetOf<String>()

    fun implementation(dependency: String) {
        dependencies.add(dependency)
    }
}