package com.dtp.fabric

import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

@KotlinScript(
    fileExtension = "fabricate.kts",
    compilationConfiguration = Config::class
)
abstract class Fabricate

object Config : ScriptCompilationConfiguration(
    body = {
        defaultImports("com.dtp.domain.Project")

        jvm {
            dependenciesFromCurrentContext("domain", wholeClasspath = true)
        }
    }
)