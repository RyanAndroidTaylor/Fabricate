package com.dtp.fabricate.runtime

import com.dtp.fabricate.runtime.models.Project
import com.dtp.fabricate.runtime.tasks.Task
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

object ObjectCreator {
    fun <T: Any> create(type: KClass<T>): T {
        val constructor: KFunction<T> = type.constructors.first()

        return constructor.call()
    }
}

class TaskCreator(val project: Project) {
    fun <T: Task> create(type: KClass<T>): T {
        val constructor: KFunction<T> = type.constructors.first()

        val task = constructor.call()

        task.project = project

        return task
    }
}