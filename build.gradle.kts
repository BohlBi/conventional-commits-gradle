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
    group = "versioning"
    description = "Displays the current project version"
    doLast {
        println("Project version: $version")
    }
}

tasks.register("installGitHooks") {
    group = "git"
    description = "Installs Git hooks for commit message validation"
    outputs.upToDateWhen { false }
    doLast {
        GitHooksInstaller.install(project.projectDir)
    }
}

tasks.register("validateCommitMessage") {
    group = "git"
    description = "Validates commit message against Conventional Commits specification"
    doLast {
        val messageFile = project.findProperty("message-file") as String?

        if (messageFile == null) {
            println("No message file provided")
            return@doLast
        }

        val file = File(messageFile)
        if (!file.exists()) {
            println("Message file not found: $messageFile")
            return@doLast
        }

        val commitMessage = file.readText().trim()
        val result = CommitMessageValidator.validate(commitMessage)

        println(result.message)

        if (!result.isValid) {
            throw GradleException("Commit message validation failed")
        }
    }
}

tasks.named("build") {
    dependsOn("installGitHooks")
}