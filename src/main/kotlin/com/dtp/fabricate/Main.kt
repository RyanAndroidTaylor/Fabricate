package com.dtp.fabricate

import com.dtp.fabricate.runtime.cli.ArgumentError
import com.dtp.fabricate.runtime.cli.ArgumentParser
import com.dtp.fabricate.runtime.daemon.SimpleDaemon
import com.dtp.fabricate.runtime.deps.SyncTask
import com.dtp.fabricate.runtime.either
import com.dtp.fabricate.runtime.models.Project
import com.dtp.fabricate.runtime.models.Settings
import com.dtp.fabricate.runtime.models.TaskContainer
import com.dtp.fabricate.runtime.tasks.*
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

fun main(vararg args: String) {
    println("Fabricate started with args ${args.joinToString()}")

    val settings = Settings()
    val settingsScriptResult = evalSettingsFile(File("./settings.fabricate.kts"), settings)

    settingsScriptResult.reports.forEach {
        if (it.severity > ScriptDiagnostic.Severity.DEBUG) {
            println("\u001B[31mError: ${it.message}" + if (it.exception == null) "" else ": ${it.exception}" + "\u001B[0m")
        }
    }

    evalProject(settings.rootProject, settings)
    settings.subProjects.values.forEach { evalProject(it, settings) }

    ArgumentParser.parse(args.asList()).either(
        onValue = { result ->
            if (result.commands.isNotEmpty()) {
                //TODO I'm thinking the options should be passed to the daemon but not 100% on that yet
                SimpleDaemon().executeCommands(result.commands, settings)
            }
        },
        onError = {
            when (it) {
                is ArgumentError.MissingArgument -> println("FAILED...\n${it.message}")
                is ArgumentError.UnknownOption -> println("FAILED...\n${it.message}")
                is ArgumentError.UnknownTask -> println("FAILED...\n${it.message}")
                is ArgumentError.MalformedCommand -> println("FAILED...\n${it.message}")
            }
        }
    )
}

private fun evalProject(project: Project, settings: Settings) {
    val buildScriptResult = evalBuildFile(
        File(project.projectDir, "/build.fabricate.kts"),
        project,
        settings,
    )

    buildScriptResult.reports.forEach {
        if (it.severity > ScriptDiagnostic.Severity.DEBUG) {
            println("\u001B[31mError: ${it.message}" + if (it.exception == null) "" else ": ${it.exception}" + "\u001B[0m")
        }
    }

    // We want to register and configure all defaults tasks before running the script.
    // This way the script can override the default configuration.
    //TODO Should we be registering the default tasks for all projects?
    project.tasks.registerDefaultTasks()
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

private fun evalBuildFile(
    scriptFile: File,
    project: Project,
    settings: Settings
): ResultWithDiagnostics<EvaluationResult> {
    val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<Fabricate> {
        jvm {
            dependenciesFromClassContext(Fabricate::class, wholeClasspath = true)
        }
    }
    val scriptEvaluationContinuation = ScriptEvaluationConfiguration {
        providedProperties(
            "project" to project,
            "settings" to settings
        )
    }

    return BasicJvmScriptingHost().eval(
        scriptFile.toScriptSource(),
        compilationConfiguration,
        scriptEvaluationContinuation,
    )
}

private fun evalSettingsFile(scriptFile: File, settings: Settings): ResultWithDiagnostics<EvaluationResult> {
    val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<Fabricate> {
        jvm {
            dependenciesFromClassContext(Fabricate::class, wholeClasspath = true)
        }
    }
    val scriptEvaluationContinuation = ScriptEvaluationConfiguration {
        providedProperties(
            //TODO We don't need Project in settings script. Need to find a way to not have to pass it
            "project" to Project("Dummy", File("Dummy")),
            "settings" to settings,
        )
    }

    return BasicJvmScriptingHost().eval(
        scriptFile.toScriptSource(),
        compilationConfiguration,
        scriptEvaluationContinuation,
    )
}