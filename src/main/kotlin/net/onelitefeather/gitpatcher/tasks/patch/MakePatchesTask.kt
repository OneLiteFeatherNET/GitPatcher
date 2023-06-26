package net.onelitefeather.gitpatcher.tasks.patch

import java.io.File
import java.util.function.Predicate
import net.onelitefeather.gitpatcher.Git
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class MakePatchesTask : PatchTask() {

    companion object {
        private val HUNK = Predicate<String> { it.startsWith("@@") }
        private const val ORIGIN_UPSTREAM = "origin/upstream"
        private const val NO_COLOR = "--no-color"
        private const val STAGED = "--staged"
        private const val DEV_NULL = "--- /dev/null"
        private const val HEAD = "HEAD"
    }

    @get:Input
    abstract val formatPatchArgs: Array<String>

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
            getCachedRef() == git.getRef()
        }
    }

    @TaskAction
    fun makePatches() {
        if (patchDir.isDirectory) {
            val patches = this.getPatches()
            if (patches.isNotEmpty()) {
                patches.forEach {
                    assert(it.delete()) { "Failed to delete old patch" }
                }
            }
        } else {
            assert(patchDir.mkdirs()) { "Failed to create patch directory" }
        }

        val git = Git(repo)
        git.formatPatch(*formatPatchArgs, "-o", patchDir.absolutePath, ORIGIN_UPSTREAM)

        git.repo = root
        git.add("-A", patchDir.absolutePath)

        didWork = false
        getPatches().forEach {
            val diff = git.diff(NO_COLOR, "-U1", STAGED, it.absolutePath).text().lines()
            if (isUpToDate(diff)) {
                logger.lifecycle("Skipping ${it.name} (up-to-date)")
                git.reset(HEAD, it.absolutePath)
                git.checkout("--", it.absolutePath)
            } else {
                didWork = true
                logger.lifecycle("Generating ${it.name}")
            }
        }
    }

    private fun isUpToDate(diff: List<String>): Boolean {
        if (diff.isEmpty()) {
            return true
        }

        if (diff.contains(DEV_NULL)) {
            return false
        }

        // Check if there are max. 2 diff hunks (once for the hash, and once for the Git version)
        val count = diff.count(HUNK::test)
        if (count == 0) {
            return true
        }

        if (count > 2) {
            return false
        }

        for (i in 0..diff.size) {
            if (HUNK.test(diff[i])) {
                val change = diff[i + 1]
                if (!change.startsWith("From", 1) && !change.startsWith("--", 1)) {
                    return false
                }
            }
        }

        return true
    }
}