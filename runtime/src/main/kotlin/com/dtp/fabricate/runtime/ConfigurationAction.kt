package com.dtp.fabricate.runtime

fun interface Action<T> {
    fun execute(item: T): Unit
}