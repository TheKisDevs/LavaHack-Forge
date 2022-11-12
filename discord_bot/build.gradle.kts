plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.50"
    id("java")
}

group = "the.kis.devs.discordbot"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    implementation("org.jetbrains.kotlin:kotlin-bom")

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    implementation("net.dv8tion:JDA:5.0.0-alpha.20")
}

/*tasks.jar {
    manifest.attributes(
        "Main-Class" to "the.kis.devs.discordbot.DiscordBotMain"
    )
    *//*duplicatesStrategy = DuplicatesStrategy.INCLUDE

    from(
        *configurations["include"].map {
            if (it.isDirectory) it else zipTree(it)
        }.toTypedArray()
    )*//*
}*/

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}