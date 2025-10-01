plugins {
    kotlin("jvm") version "2.2.10"
    id("com.gradleup.shadow") version "9.2.2"
}

group = "com.dtp.fabric"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))
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
    destinationDirectory = File("example/")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "com.dtp.fabric.MainKt")
    }
}