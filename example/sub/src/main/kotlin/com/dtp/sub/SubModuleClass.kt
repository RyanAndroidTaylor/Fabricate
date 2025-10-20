package com.dtp.sub

class SubModuleClass(val items: List<String>) {
    fun info() {
        println("Item Info:")
        items.forEach {
            println("    $it")
        }
    }
}