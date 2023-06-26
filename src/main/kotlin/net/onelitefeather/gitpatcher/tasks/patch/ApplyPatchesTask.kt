package net.onelitefeather.gitpatcher.tasks.patch

import java.io.File
import net.onelitefeather.gitpatcher.Git
import net.onelitefeather.gitpatcher.tasks.UpdateSubmodulesTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class ApplyPatchesTask : PatchTask() {

    abstract val branch: String
    abstract val updateTask: UpdateSubmodulesTask

    companion object {
        private const val FORCE = "-f"
        private const val UPSTREAM = "upstream"
        private const val RECURSIVE = "--recursive"
        private const val ORIGIN = "origin"
        private const val ORIGIN_UPSTREAM = "origin/upstream"
        private const val HARD = "--hard"
        private const val GPGSIGN = "commit.gpgsign"
        private const val ABORT = "--abort"
        private const val AM_3WAY = "--3way"
    }

    @InputFiles
    override fun getPatches(): Array<File> {
        return super.getPatches()
    }

    @OutputDirectory
    fun getRepo(): File {
        return this.repo
    }

    @OutputFile
    override fun getRefCache(): File {
        return super.getRefCache()
    }

    init {
        outputs.upToDateWhen {
            if (!repo.isDirectory) {
                return@upToDateWhen false
            }

            val git = Git(repo)
            git.getStatus().isEmpty() && getCachedRef() == git.getRef() && getCachedSubmodules() == updateTask.getRef()
        }
    }

    @TaskAction
    fun applyPatches() {
        val git = Git(getSubmoduleRoot())
        git.branch(FORCE, UPSTREAM)

        val gitDir = File(repo, ".git")
        if (!gitDir.isDirectory || gitDir.list().isEmpty()) {
            logger.lifecycle("Creating $repo repository...")
            assert(gitDir.delete())
            git.repo = root
            git.clone(RECURSIVE, submodule, repo.absolutePath, "-b", UPSTREAM)
        }

        logger.lifecycle("Resetting $repo...")

        git.repo = repo
        git.fetch(ORIGIN)
        git.checkout("-B", branch, ORIGIN_UPSTREAM)
        git.reset(HARD)

        if (!patchDir.isDirectory) {
            assert(patchDir.mkdirs()) { "Failed to create patch directory" }
        }

        if ("true".equals(git.config("commit.gpgsign").text(), true)) {
            logger.warn("Disabling GPG signing for the gitpatcher repository")
            git.config(GPGSIGN, "false")
        }

        val patches = this.getPatches()
        if (patches.isNotEmpty()) {
            logger.lifecycle("Applying patches from $patchDir to $repo")
            git.am(ABORT)
            git.am(AM_3WAY, *patches.map { it.absolutePath }.toTypedArray())
            logger.lifecycle("Successfully applied patches from $patchDir to $repo")
        }

        getRefCache().writeText("${git.getRef()}\n${updateTask.getRef()}")
    }
}