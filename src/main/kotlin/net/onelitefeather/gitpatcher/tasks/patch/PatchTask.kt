package net.onelitefeather.gitpatcher.tasks.patch

import net.onelitefeather.gitpatcher.tasks.GitTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import java.io.File

abstract class PatchTask : GitTask() {

    abstract val root: File

    @get:Internal
    abstract val patchDir: File

}