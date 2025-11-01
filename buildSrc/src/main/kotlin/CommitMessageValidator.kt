import java.io.File

object CommitMessageValidator {
    // Matches: type(scope)?: subject oder type!: subject - type aus Liste,
    // scope optional in Klammern,
    // ! für Breaking Changes, subject mindestens 1 Zeichen
    private val conventionalCommitPattern = Regex(
        "^(feat|fix|docs|style|refactor|perf|test|build|ci|chore|revert)(\\(.+\\))?!?: .{1,}$",
        RegexOption.IGNORE_CASE
    )

    private val validTypes = listOf(
        "feat" to "A new feature",
        "fix" to "A bug fix",
        "docs" to "Documentation only changes",
        "style" to "Code style changes",
        "refactor" to "Code refactoring",
        "perf" to "Performance improvements",
        "test" to "Adding or updating tests",
        "build" to "Build system changes",
        "ci" to "CI/CD changes",
        "chore" to "Other changes",
        "revert" to "Revert a previous commit"
    )

    fun validate(message: String): ValidationResult {
        if (message.startsWith("Merge")) {
            return ValidationResult(true, "Merge commit - skipping validation")
        }

        val firstLine = message.lines().firstOrNull()?.trim() ?: ""

        if (!conventionalCommitPattern.matches(firstLine)) {
            return ValidationResult(false, buildErrorMessage(firstLine))
        }

        return ValidationResult(true, "Commit message is valid")
    }

    private fun buildErrorMessage(message: String): String {
        val typesList = validTypes.joinToString("\n") { (type, desc) ->
            "  ${type.padEnd(10)} - $desc"
        }

        return """
            |
            |ERROR: Commit message does not follow Conventional Commits
            |
            |Format: <type>(<scope>): <subject>
            |
            |Valid types:
            |$typesList
            |
            |Examples:
            |  feat: add user authentication
            |  fix(api): resolve null pointer exception
            |  feat!: breaking change in API
            |  docs: update README
            |
            |Your commit message:
            |  $message
            |
        """.trimMargin()
    }

    data class ValidationResult(val isValid: Boolean, val message: String)
}