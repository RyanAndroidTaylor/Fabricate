package com.dtp.fabricate

import com.dtp.fabricate.runtime.cli.Argument
import com.dtp.fabricate.runtime.cli.ArgumentError
import com.dtp.fabricate.runtime.cli.ArgumentParser
import com.dtp.fabricate.runtime.deps.SyncTask
import com.dtp.fabricate.runtime.either
import com.dtp.fabricate.runtime.models.Project
import com.dtp.fabricate.runtime.models.TaskContainer
import com.dtp.fabricate.runtime.tasks.BuildTask
import com.dtp.fabricate.runtime.tasks.InfoTask
import com.dtp.fabricate.runtime.tasks.JarTask
import com.dtp.fabricate.runtime.tasks.RunTask
import com.dtp.fabricate.runtime.tasks.ZipTask
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
    println("Fabricate started with args ${args.joinToString()}")

    ArgumentParser.parse(args.asList()).either(
        onValue = { arguments ->
            // We want to register and configure all defaults tasks before running the script.
            // This way the script can override the default configuration.
            Project.tasks.registerDefaultTasks()

            val buildScript = File("./build.fabricate.kts")

            val res = evalFile(buildScript)

            res.reports.forEach {
                if (it.severity > ScriptDiagnostic.Severity.DEBUG) {
                    println("\u001B[31mError: ${it.message}" + if (it.exception == null) "" else ": ${it.exception}" + "\u001B[0m")
                }
            }

            with(Project.tasks) {
                when (arguments.firstOrNull()) {
                    Argument.Info -> enqueueTask("info")
                    Argument.Build -> enqueueTask("build")
                    Argument.Jar -> enqueueTask("jar")
                    Argument.Run -> enqueueTask("Run")
                    Argument.Sync -> enqueueTask("sync")
                    is Argument.Zip -> enqueueTask("zip")

                    else -> {
                        println("No arguments were passed so no work was done")

                        null
                    }
                }

                Project.tasks.generateTaskGraph().forEach { provider ->
                    provider.task.run()
                }
            }
        },
        onError = {
            when (it) {
                ArgumentError.ConflictingArguments ->
                    println("FAILED...\n Founding conflicting arguments.")

                is ArgumentError.MissingArgument ->
                    println("FAILED...\n${it.message}")
            }
        }
    )
}

private fun TaskContainer.registerDefaultTasks() {
    register("info", InfoTask::class)

    register("sync", SyncTask::class)

    register("build", BuildTask::class) {
        dependsOn("sync")
    }

    register("run", RunTask::class) {
        dependsOn("build")
    }

    register("jar", JarTask::class) {
        dependsOn("build")
    }

    register("zip", ZipTask::class)
}

private fun evalFile(scriptFile: File): ResultWithDiagnostics<EvaluationResult> {
    val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<Fabricate>() {
        jvm {
            dependenciesFromClassContext(Fabricate::class, wholeClasspath = true)
        }
    }
    return BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), compilationConfiguration, null)
}
