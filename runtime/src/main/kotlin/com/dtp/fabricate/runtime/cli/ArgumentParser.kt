package com.dtp.fabricate.runtime.cli

import com.dtp.fabricate.runtime.Either

object ArgumentParser {
    fun parse(args: List<String>): Either<List<Argument>, ArgumentError> {
        val finalArguments = mutableListOf<Argument>()

        var i = 0

        while (i <= args.lastIndex) {
            when (val argument = args[i]) {
                "-sync" ->
                    finalArguments.add(Argument.Sync)
                "-build" -> {
                    if (hasConflictingArguments(argument, args)) {
                        return Either.Error(ArgumentError.ConflictingArguments)
                    } else {
                        finalArguments.add(Argument.Build)
                    }
                }
                "-run" -> {
                    if (hasConflictingArguments(argument, args)) {
                        return Either.Error(ArgumentError.ConflictingArguments)
                    } else {
                        finalArguments.add(Argument.Run)
                    }
                }
                "-zip" -> {
                    if (args.lastIndex <= i)  {
                        return Either.Error(ArgumentError.MissingArgument("-zip requires a file to compress"))
                    }

                    finalArguments.add(Argument.Zip(args[i + 1]))

                    i++
                }
            }

            i++
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