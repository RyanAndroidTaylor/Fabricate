package com.dtp.fabricate

import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm

@KotlinScript(
    fileExtension = "fabricate.kts",
    compilationConfiguration = Config::class
)
abstract class Fabricate

object Config : ScriptCompilationConfiguration(
    body = {
        defaultImports(
            "com.dtp.fabricate.runtime.models.Project.*",
            "com.dtp.fabricate.runtime.tasks.Task",
            "com.dtp.fabricate.runtime.tasks.AbstractTask",
            "com.dtp.fabricate.runtime.tasks.JarTask.*",
            "com.dtp.fabricate.runtime.models.TaskContainer.*"
        )

        jvm {
            dependenciesFromClassContext(Fabricate::class, wholeClasspath = true)
        }
    }
)