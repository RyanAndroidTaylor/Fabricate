package com.dtp.fabricate.runtime.cli

sealed interface ArgumentError {
    data object ConflictingArguments : ArgumentError
    data class MissingArgument(val message: String) : ArgumentError
}