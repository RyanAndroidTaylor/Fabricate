name = "Example"
projectPackage = "com.dtp.example"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.2")
}

tasks.jar {
    mainClass = "Example.kt"
}