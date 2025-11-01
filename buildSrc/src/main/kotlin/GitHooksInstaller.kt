import java.io.File

object GitHooksInstaller {

    fun install(projectDir: File) {
        val gitDir = File(projectDir, ".git")
        if (!gitDir.exists()) {
            println("WARNING: .git directory not found - skipping git hooks installation")
            return
        }

        val hooksDir = File(gitDir, "hooks")
        hooksDir.mkdirs()

        if (isHookUpToDate(hooksDir)) {
            return
        }

        installCommitMsgHook(hooksDir)
        println("Git hooks installed successfully")
    }

    private fun isHookUpToDate(hooksDir: File): Boolean {
        val commitMsgHook = File(hooksDir, "commit-msg")
        if (!commitMsgHook.exists()) {
            return false
        }

        val currentContent = commitMsgHook.readText()
        val expectedContent = getHookContent()

        return currentContent == expectedContent
    }

    private fun installCommitMsgHook(hooksDir: File) {
        val commitMsgHook = File(hooksDir, "commit-msg")
        commitMsgHook.writeText(getHookContent())
        commitMsgHook.setExecutable(true)
    }

    private fun getHookContent(): String {
        return """
            #!/bin/sh
            
            COMMIT_MSG_FILE=${'$'}1
            
            ./gradlew validateCommitMessage --message-file="${'$'}COMMIT_MSG_FILE" --quiet
            
            exit ${'$'}?
        """.trimIndent()
    }
}