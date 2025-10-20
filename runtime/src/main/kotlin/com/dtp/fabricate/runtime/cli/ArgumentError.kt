package com.dtp.fabricate.runtime.cli

sealed interface ArgumentError {
    data class MissingArgument(val message: String) : ArgumentError
    data class UnknownOption(val message: String) : ArgumentError
    data class UnknownTask(val message: String) : ArgumentError
    data class MalformedCommand(val message: String) : ArgumentError
}