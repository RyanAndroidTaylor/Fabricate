import java.io.File

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.2")
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation(project("sub"))
}

tasks.jar {
    mainClass = "com.dtp.example.Example.kt"
}

tasks.named<ZipTask>("zip") {
    root = File("./src")
}

tasks.register("MyTask", MyTask::class) {
    println("Configuring MyTask")

    dependsOn("build")
}

class MyTask : AbstractTask() {
    override fun execute() {
        println("Running MyTask")
    }
}
