dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
}

tasks.jar {
    mainClass = "Main.kt"
}

tasks.register<MyTask>("MyTask", MyTask::class) {
    println("Configuring MyTask")
}

class MyTask : AbstractTask() {
    override fun execute() {
        println("Running MyTask")
    }
}
