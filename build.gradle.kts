plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.publication")
    kotlin("plugin.serialization")
}

dependencies {
    slim(kotlin("stdlib-jdk8"))
    slim("org.jetbrains.kotlinx:kotlinx-serialization-json")
    slim("com.charleskorn.kaml:kaml")

    implementation("com.mineinabyss:idofront:1.17.1-0.6.23")
}
