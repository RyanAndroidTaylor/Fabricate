package com.dtp.fabricate.runtime.cli

import com.dtp.fabricate.runtime.Either

/**
 * Tasks: Start with the module (:module:MyTask) or if no modules is specified (MyTask) it is
 * run in the root of the project.
 *
 * Options: Start with a double hyphen (--option) and can come before or after a task (--option1 :module:MyTask --option2)
 */
object ArgumentParser {
    fun parse(args: List<String>): Either<ParseResult, ArgumentError> {
        val options = mutableListOf<Option>()
        val commands = mutableListOf<CliCommand>()

        var i = 0

        while (i <= args.lastIndex) {
            val argument = args[i]

            if (argument.startsWith("--")) {
                when (argument) {
                    "--info" -> options.add(Option.Info)
                    else ->
                        return Either.Error(ArgumentError.UnknownOption("Unknown option $argument"))
                }
            } else {
                if (argument.contains(':')) {
                    val pair = argument.split(':')

                    if (pair.size != 2) {
                        return Either.Error(ArgumentError.MalformedCommand("MalformedCommand: Currently supported formats \"command\" | \"module:command\" but found $argument"))
                    }

                    commands.add(CliCommand(pair[0], pair[1]))
                } else {
                    commands.add(CliCommand(null, argument))
                }
            }

            i++
        }

        return Either.Value(ParseResult(commands, options))
    }
}

data class CliCommand(val project: String?, val command: String)

data class ParseResult(val commands: List<CliCommand>, val options: List<Option>)