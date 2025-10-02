package com.dtp.fabricate.runtime.models

object Project {
    var name: String = ""

    fun project(block: Project.() -> Unit) {
        this.block()
    }
}