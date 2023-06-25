package net.onelitefeather.gitpatcher.tasks

import org.gradle.api.tasks.Input

abstract class SubmoduleTask : GitTask() {
    @get:Input
    abstract val submodule: String
}