plugins {
    kotlin("jvm") version "2.2.10"
    id("com.gradleup.shadow") version "9.2.2"
}

group = "com.dtp.fabricate"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":runtime"))
    implementation(project(":script-definition"))

    implementation("org.jetbrains.kotlin:kotlin-scripting-common")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(18)
}
java {
    withSourcesJar()
}

tasks.shadowJar {
    destinationDirectory = File("example/fabricate/")

    dependsOn()
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "com.dtp.fabricate.MainKt")
    }
}