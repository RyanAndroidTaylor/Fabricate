package com.dtp.fabricate

import com.dtp.fabricate.runtime.FabricError
import com.dtp.fabricate.runtime.cli.Argument
import com.dtp.fabricate.runtime.cli.ArgumentParser
import com.dtp.fabricate.runtime.either
import com.dtp.fabricate.runtime.models.Project
import com.dtp.fabricate.runtime.tasks.BuildTask
import com.dtp.fabricate.runtime.tasks.RunTask
import com.dtp.fabricate.runtime.tasks.Task
import java.io.File
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

fun main(vararg args: String) {
    println("Starting Fabricate....")

    ArgumentParser.parse(args.asList()).either(
        onValue = { arguments ->
            val buildScript = File("./build.fabricate.kts")

            val res = evalFile(buildScript)

            res.reports.forEach {
                if (it.severity > ScriptDiagnostic.Severity.DEBUG) {
                    println(" : ${it.message}" + if (it.exception == null) "" else ": ${it.exception}")
                }
            }

            val task: Task? = when {
                arguments.contains(Argument.Build) -> BuildTask(Project)
                arguments.contains(Argument.Run) -> RunTask(Project)
                else -> {
                    println("No arguments were passed so no work was done")

                    null
                }
            }

            task?.run()
        },
        onError = {
            when (it) {
                FabricError.ConflictingArguments ->
                    println("FAILED...\n Founding conflicting arguments.")
            }
        }
    )
}

private fun evalFile(scriptFile: File): ResultWithDiagnostics<EvaluationResult> {
    val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<Fabricate>() {
        jvm {
            dependenciesFromClassContext(Fabricate::class, wholeClasspath = true)
        }
    }
    return BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), compilationConfiguration, null)
}
