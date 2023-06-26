package net.onelitefeather.gitpatcher.tasks

import net.onelitefeather.gitpatcher.Git
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class FindGitTask : DefaultTask() {

    @get:Input
    abstract val submodule: String

    @TaskAction
    fun findGit() {
        val git = Git(project.rootDir)
        try {
            val version = git.gitVersion().text().lines().joinToString()
            logger.lifecycle("Using $version for patching submodule $submodule")
        } catch (e: Throwable) {
            throw UnsupportedOperationException(
                "Failed to verify Git version. Make sure running the Gradle build in an environment where Git is in your PATH.", e
            )
        }
    }
}