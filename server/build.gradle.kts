plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.50"
    `java-library`
}

repositories {
    mavenCentral()
}

configurations.create("include")

dependencies {
    "include"(platform("org.jetbrains.kotlin:kotlin-bom"))
    "include"("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    "include"("org.json:json:20210307")
    "include"("com.mashape.unirest:unirest-java:1.4.9")
    "include"("org.apache.httpcomponents:httpmime:4.5.13")
    "include"("org.apache.httpcomponents:httpcore-nio:4.4.14")
    "include"("org.apache.httpcomponents:httpcore:4.4.14")
    "include"("org.apache.httpcomponents:httpclient:4.5.13")
    "include"("org.apache.httpcomponents:httpasyncclient:4.1.4")
    "include"("com.googlecode.json-simple:json-simple:1.1.1")
    "include"("org.slf4j:slf4j-api:1.7.25")

    implementation(configurations["include"])

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation(kotlin("test"))
}

tasks.jar {
    manifest.attributes(
        "Main-Class" to "the.kis.devs.server.MainKt"
    )
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    from(
        *configurations["include"].map {
            if (it.isDirectory) it else zipTree(it)
        }.toTypedArray()
    )
}

tasks.test {
    useJUnitPlatform()
}