package net.onelitefeather.gitpatcher.tasks

import java.io.File
import net.onelitefeather.gitpatcher.Git
import org.gradle.api.tasks.TaskAction

class UpdateSubmodulesTask(
    override val repo: File,
    override val submodule: String) : SubmoduleTask() {

    companion object {
        private const val STATUS = "status"
        private const val UPDATE = "update"
        private const val INIT = "--init"
        private const val RECURSIVE = "--recursive"
    }

    private lateinit var ref: String

    @TaskAction
    fun updateSubmodules() {
        val git = Git(repo)
        val result = git.submodule(STATUS, "--", submodule).text()

        val stringBuilder = StringBuilder()
        for (i in 1 until result.indexOf(" ", 1)) {
            stringBuilder.append(result[i])
        }

        this.ref = stringBuilder.toString()

        if (result.startsWith(" ")) {
            didWork = false
            return
        }

        git.submodule(UPDATE, INIT, RECURSIVE)
    }

    fun getRef(): String {
        return this.ref
    }
}
