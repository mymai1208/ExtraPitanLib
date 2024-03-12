plugins {
    kotlin("jvm") version "1.9.22"
    id("fabric-loom") version "1.5-SNAPSHOT"
}

val mod_version: String by project

group = "net.mymai1208"
version = mod_version

val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val fabric_kotlin_version: String by project
val fabric_api_version: String by project
val mcpitanlib_version: String by project

repositories {
    mavenCentral()

    maven("https://maven.pitan76.net/")
    maven( "https://maven.shedaniel.me/")
    maven("https://maven.architectury.dev/")
    maven("https://maven.mymai1208.net/")
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings("net.fabricmc:yarn:${yarn_mappings}")

    modImplementation("net.fabricmc:fabric-loader:${loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_api_version}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${fabric_kotlin_version}")

    modImplementation("net.pitan76:mcpitanlib-fabric+${mcpitanlib_version}")

    modImplementation("net.mymai1208:mc-serializer-mod:0.0.3")
}

tasks {
    processResources {
        filesMatching("fabric.mod.json") {
            expand(mapOf(
                "version" to mod_version,
                "fabric_kotlin_version" to fabric_kotlin_version,
                "fabric_loader_version" to loader_version
            ))
        }
    }

    test {
        useJUnitPlatform()
    }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}