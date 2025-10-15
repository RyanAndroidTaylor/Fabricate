package com.dtp.example

import com.dtp.example.other.Other
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch

fun main() {
    println("Example App")

//    CoroutineScope(Dispatchers.Main).launch {
//        println("Inside Coroutine")
//    }

    Other.logOther()
}
