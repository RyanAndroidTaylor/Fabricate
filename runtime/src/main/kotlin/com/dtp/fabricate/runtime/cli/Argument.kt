package com.dtp.fabricate.runtime.cli

sealed interface Argument {
    object Jar : Argument
    object Run : Argument
    object Sync : Argument
    object Info : Argument

    data class Zip(val file: String) : Argument
}