val installGitHooks by tasks.registering(Copy::class) {
    val fromDir = "scripts/integration/git/hooks"
    val toDir = ".git/hooks"

    from(File(rootDir, fromDir))
    include("*")
    into(File(rootDir, toDir))

    rename("\\.sh", "")

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

project.afterEvaluate {
    if (project.propertyValue<Boolean>(Config.ProjectProperties.installGitHooks) == true) {
        tasks["preBuild"].dependsOn(installGitHooks)
    }
}
