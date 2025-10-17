name = "Fabricate"
projectPackage = "com.dtp.fabricate"

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

tasks.enqueueTask("MyTask")

class MyTask : AbstractTask() {
    override fun run() {
        println("Running MyTask")
    }
}
