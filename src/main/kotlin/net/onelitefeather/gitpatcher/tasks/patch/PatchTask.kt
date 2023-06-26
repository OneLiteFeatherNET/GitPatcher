package net.onelitefeather.gitpatcher.tasks.patch

import org.gradle.api.tasks.Internal
import java.io.File
import net.onelitefeather.gitpatcher.tasks.SubmoduleTask

abstract class PatchTask : SubmoduleTask() {

    abstract val root: File

    @get:Internal
    abstract val patchDir: File

    private var cachedRefs: List<String>? = null

    protected open fun getPatches(): Array<File> {
        if (!patchDir.isDirectory) {
            return emptyArray()
        }

        return patchDir.listFiles { _, name ->
            name.endsWith(".patch")
        }.sortedArray()
    }

    fun getSubmoduleRoot(): File {
        return File(root, submodule)
    }

    fun getGitDir(): File {
        return File(repo, ".git")
    }

    open fun getRefCache(): File {
        return File(this.getGitDir(), ".gitpatcher_ref")
    }

    private fun readCache() {
        if (cachedRefs == null) {
            val refCache = this.getRefCache()
            if (refCache.isFile) {
                this.cachedRefs = refCache.readLines()
                    .map { it.trim() }
                    .filter {
                        it.isNotEmpty() && !it.startsWith("#")
                    }
            } else {
                this.cachedRefs = emptyList()
            }
        }
    }

    fun getCachedRef(): String? {
        this.readCache()
        return cachedRefs?.get(0)
    }

    fun getCachedSubmodules(): String? {
        this.readCache()
        return cachedRefs?.get(1)
    }
}