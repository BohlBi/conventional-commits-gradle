plugins {
    id("java")
    id("com.palantir.git-version") version "4.1.0"
}

group = "org.example"

val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion().removePrefix("v")

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("showVersion") {
    doLast {
        println("Project version: $version")
    }
}