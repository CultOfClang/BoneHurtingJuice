import Com_mineinabyss_conventions_platform_gradle.Deps

val idofrontVersion: String by project

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.publication")
    id("com.mineinabyss.conventions.copyjar")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
    maven("https://repo.mineinabyss.com/releases")
}

dependencies {

    // From Geary
    compileOnly(Deps.kotlinx.serialization.json)
    compileOnly(Deps.kotlinx.serialization.kaml)

    implementation("com.mineinabyss:idofront:$idofrontVersion")
}
