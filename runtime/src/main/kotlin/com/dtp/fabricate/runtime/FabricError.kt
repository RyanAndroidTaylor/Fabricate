package com.dtp.fabricate.runtime

sealed interface FabricError {
    data object ConflictingArguments : FabricError
}