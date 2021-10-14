
val idofrontVersion: String by project

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.slimjar")
    id("com.mineinabyss.conventions.publication")
    id("com.mineinabyss.conventions.copyjar")
    kotlin("plugin.serialization")
}

dependencies {

    // From Geary
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json")
    compileOnly("com.charleskorn.kaml:kaml")

    implementation("com.mineinabyss:idofront:$idofrontVersion")
}
