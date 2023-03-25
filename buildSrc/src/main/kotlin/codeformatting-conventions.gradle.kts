plugins {
    id("com.diffplug.spotless")
}

val ktlintVersion = "0.48.2"

spotless {
    encoding = Charsets.UTF_8

    if (file("${rootProject.path}/.git").exists()) {
        ratchetFrom = "origin/main"
    }

    format("misc") {
        target(
            fileTree(
                mapOf(
                    "dir" to projectDir.absolutePath,
                    "include" to listOf(
                        "**/*.md",
                        "**/.gitignore",
                        "**/*.yaml",
                        "**/*.yml"
                    ),
                    "exclude" to listOf(
                        ".gradle/**",
                        ".idea/**",
                        ".gradle-cache/**",
                        "**/tools/**",
                        "**/build/**"
                    )
                )
            )
        )

        prettier()

        trimTrailingWhitespace()
        endWithNewline()
        indentWithSpaces(4)
    }

    format("xml") {
        target("src/**/res/**/*.xml")

        trimTrailingWhitespace()
        endWithNewline()
        indentWithSpaces(4)
    }

    kotlin {
        target("src/**/*.kt")

        ktlint(ktlintVersion).userData(
            mapOf(
                "continuation_indent_size" to "4"
            )
        )
        trimTrailingWhitespace()
        endWithNewline()
        indentWithSpaces(4)
    }

    kotlinGradle {
        target("**/*.kts", "*.kts")

        ktlint(ktlintVersion).userData(
            mapOf(
                "continuation_indent_size" to "4"
            )
        )
        trimTrailingWhitespace()
        endWithNewline()
        indentWithSpaces(4)
    }
}

allprojects {
    afterEvaluate {
        if (project.propertyValue<Boolean>(Config.ProjectProperties.codeFormattingEnabled) == true) {
            val hookTask = tasks.findByName("preBuild")
                ?: tasks.findByName("compileKotlin")
                ?: tasks.findByName("compileJava")
                ?: tasks.findByName("compileGroovy")
                ?: tasks.findByName("compile")
                ?: tasks.findByName("assembleDebug")
                ?: tasks.findByName("assemble")

            tasks.findByName("spotlessApply")?.let { spotlessApply ->
                requireNotNull(hookTask) {
                    "No hook tasks found. To use spotless, make sure the above list of hook task has at-least one task used by this module."
                }

                hookTask.dependsOn(spotlessApply)
            }
        }
    }
}
