package com.dtp.fabricate.runtime.cli

sealed interface Argument {
    data object Jar : Argument
    data object Run : Argument
    data object Sync : Argument
    data object Build : Argument
    data object Info : Argument
    data object Zip : Argument
}