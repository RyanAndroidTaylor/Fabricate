package com.dtp.fabricate.runtime.cli

sealed interface Argument {
    object Build : Argument
    object Run : Argument
}