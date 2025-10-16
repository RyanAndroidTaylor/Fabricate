package com.dtp.fabricate.runtime

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

object ObjectCreator {
    fun <T: Any> create(type: KClass<T>): T {
        val constructor: KFunction<T> = type.constructors.first()

        return constructor.call()
    }
}