name = "Example"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

tasks.jar {
    mainPackage = "com.dtp.example"
    mainClass = "Example.kt"
}