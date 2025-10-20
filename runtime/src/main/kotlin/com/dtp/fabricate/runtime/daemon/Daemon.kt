package com.dtp.fabricate.runtime.daemon

import com.dtp.fabricate.runtime.cli.CliCommand
import com.dtp.fabricate.runtime.models.Settings

interface Daemon {
    fun executeCommands(
        commands: List<CliCommand>,
        settings: Settings
    )
}