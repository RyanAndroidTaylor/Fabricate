package com.dtp.fabricate.runtime.cli

import com.dtp.fabricate.runtime.Either
import com.dtp.fabricate.runtime.FabricError

object ArgumentParser {
    fun parse(args: List<String>): Either<List<Argument>, FabricError> {
        val finalArguments = mutableListOf<Argument>()

        args.forEach { argument ->
            when (argument) {
                "-build" -> {
                    if (hasConflictingArguments(argument, args)) {
                        return Either.Error(FabricError.ConflictingArguments)
                    } else {
                        finalArguments.add(Argument.Build)
                    }
                }
                "-run" -> {
                    if (hasConflictingArguments(argument, args)) {
                        return Either.Error(FabricError.ConflictingArguments)
                    } else {
                        finalArguments.add(Argument.Run)
                    }
                }
            }
        }

        return Either.Value(finalArguments)
    }

    private fun hasConflictingArguments(argument: String, arguments: List<String>): Boolean {
        return when (argument) {
            "-build" -> arguments.contains("-run")
            "-run" -> arguments.contains("-build")
            else -> false
        }
    }
}