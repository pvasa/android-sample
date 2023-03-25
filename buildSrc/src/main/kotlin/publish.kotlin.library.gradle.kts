plugins {
    `maven-publish`
}

group = Config.Project.group
version = Config.Project.versionName

publishing {
    publications {
        create<MavenPublication>(project.name) {
            artifactId = project.name
            from(components["kotlin"])
            tasks.findByName("androidSourcesJar")?.let(::artifact)
            tasks.findByName("androidJavadocsJar")?.let(::artifact)

            /*pom.withXml {
                val dependencies = asNode().appendNode("dependencies")
                configurations.getByName("_releaseCompile")
                    .resolvedConfiguration
                    .firstLevelModuleDependencies
                    .forEach {
                        dependencies.appendNode("dependency")
                            .appendNode("groupId", it.moduleGroup)
                            .appendNode("artifactId", it.moduleName)
                            .appendNode("version", it.moduleVersion)
                    }
            }*/
        }
    }

    repositories {
        maven {
            name = rootProject.name
            url = uri("file://$buildDir/repo")
        }
    }
}

val cleanup: Task by tasks.creating {

    tasks.getByName("publishToMavenLocal").dependsOn(this)

    val pub =
        project.publishing
            .publications
            .firstOrNull()
            as? MavenPublication
            ?: return@creating

    val repository = "$buildDir/repo"
    val groupPath = pub.groupId.replace('.', '/')

    File("$repository/$groupPath/$pub.artifactId").deleteRecursively()
}
