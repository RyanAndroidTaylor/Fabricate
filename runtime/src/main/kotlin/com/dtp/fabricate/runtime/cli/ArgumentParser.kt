package com.dtp.fabricate.runtime.cli

import com.dtp.fabricate.runtime.Either
import com.dtp.fabricate.runtime.models.Project
import com.dtp.fabricate.runtime.tasks.ZipTask
import java.io.File

object ArgumentParser {
    fun parse(args: List<String>): Either<List<Argument>, ArgumentError> {
        val finalArguments = mutableListOf<Argument>()

        var i = 0

        while (i <= args.lastIndex) {
            when (val argument = args[i]) {
                "--info" ->
                    finalArguments.add(Argument.Info)

                "sync" ->
                    finalArguments.add(Argument.Sync)

                "build" ->
                    finalArguments.add(Argument.Build)

                "jar" -> {
                    if (hasConflictingArguments(argument, args)) {
                        return Either.Error(ArgumentError.ConflictingArguments)
                    } else {
                        finalArguments.add(Argument.Jar)
                    }
                }

                "run" -> {
                    if (hasConflictingArguments(argument, args)) {
                        return Either.Error(ArgumentError.ConflictingArguments)
                    } else {
                        finalArguments.add(Argument.Run)
                    }
                }

                "zip" -> {
                    if (args.lastIndex <= i) {
                        return Either.Error(ArgumentError.MissingArgument("-zip requires a file to compress"))
                    }

                    Project.tasks.named<ZipTask>("zip") {
                        root = File(args[i + 1])
                    }

                    finalArguments.add(Argument.Zip)

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