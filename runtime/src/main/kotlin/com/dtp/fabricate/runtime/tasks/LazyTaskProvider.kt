package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.Action
import com.dtp.fabricate.runtime.TaskCreator
import kotlin.reflect.KClass
import kotlin.reflect.KProperty0

class LazyTaskProvider<T : Task>(
    override val name: String,
    val taskType: KClass<T>,
    val taskCreator: TaskCreator,
    configAction: Action<T>? = null
) : TaskProvider<T> {

    private val configActions = mutableListOf<Action<T>>()

    init {
        configAction?.let { configActions.add(it) }
    }

    override val task: T by lazy {
        taskCreator.create(taskType).also { item ->
            configActions.forEach { it.execute(item) }
        }
    }

    override fun configure(configAction: Action<T>) {
        if (::task.isLazyInitialized()) {
            configAction.execute(task)
        } else {
            configActions.add(configAction)
        }
    }
}

fun KProperty0<*>.isLazyInitialized(): Boolean {
    if (this !is Lazy<*>) return true

    return this.isInitialized()
}