package com.dtp.fabricate.runtime

fun interface Action<T> {
    fun action(item: T): Unit
}