package net.onelitefeather.gitpatcher.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import java.io.File

abstract class GitTask : DefaultTask() {

    @get:Internal
    abstract val repo: File
}