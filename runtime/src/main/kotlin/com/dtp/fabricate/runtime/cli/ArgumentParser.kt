package com.dtp.fabricate.runtime.cli

import com.dtp.fabricate.runtime.Either
import com.dtp.fabricate.runtime.models.Project

/**
 * Tasks: Start with the module (:module:MyTask) or if no modules is specified (MyTask) it is
 * run in the root of the project.
 *
 * Options: Start with a double hyphen (--option) and can come before or after a task (--option1 :module:MyTask --option2)
 */
object ArgumentParser {

    fun parse(args: List<String>): Either<ParseResult, ArgumentError> {
        val options = mutableListOf<Option>()
        val tasks = mutableListOf<String>()

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
                if (Project.tasks.hasTask(argument)) {
                    tasks.add(argument)
                } else {
                    return Either.Error(ArgumentError.UnknownTask("Unknown Task $argument"))
                }
            }

            i++
        }

        return Either.Value(ParseResult(tasks, options))
    }
}

data class ParseResult(val tasks: List<String>, val options: List<Option>)