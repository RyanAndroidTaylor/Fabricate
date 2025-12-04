package com.dtp.example

import com.dtp.example.other.Other
import com.dtp.sub.SubModuleClass
import retrofit2.Call
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch

fun main() {
    var call: Call<String>? = null
    println("Example App")

//    CoroutineScope(Dispatchers.Main).launch {
//        println("Inside Coroutine")
//    }

    println("Call: $call")
    Other.logOther()
    SubModuleClass.info()
}
