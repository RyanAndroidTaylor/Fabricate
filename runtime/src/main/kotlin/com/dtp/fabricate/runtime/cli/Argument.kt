package com.dtp.fabricate.runtime.cli

sealed interface Argument {
    object Build : Argument
    object Run : Argument
    object ListCachedDeps : Argument
    data class Zip(val file: String) : Argument
}