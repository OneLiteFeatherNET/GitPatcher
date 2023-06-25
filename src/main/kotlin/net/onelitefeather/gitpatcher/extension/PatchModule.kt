package net.onelitefeather.gitpatcher.extension

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import java.io.File

abstract class PatchModule {

    @get:Input
    abstract val submodule: String
    @get:Input
    abstract val root: File
    @get:Input
    abstract val target: File
    @get:Input
    abstract val patches: File

    @get:Input
    abstract val committerNameOverride: Property<String>
    @get:Input
    abstract val committerEmailOverride: Property<String>

}