package com.dtp.fabricate.runtime.cli

sealed interface Option {
    object Info : Option
}