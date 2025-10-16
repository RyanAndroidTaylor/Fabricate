package com.dtp.fabricate.runtime.tasks

import com.dtp.fabricate.runtime.Action
import com.dtp.fabricate.runtime.ObjectCreator
import kotlin.reflect.KClass

class LazyTaskProvider<T : Task>(
    override val name: String,
    val taskType: KClass<T>,
    private var configAction: (T.() -> Unit)? = null
) : TaskProvider<T> {

    private val configActions = mutableListOf<Action<T>>()

    init {
        configAction?.let { configActions.add(it) }
    }

    override val task: T by lazy {
        ObjectCreator.create(taskType).apply {
            configAction?.invoke(this)
        }
    }

    override fun configure(configAction: Action<T>) {
        configActions.add(configAction)
    }
}