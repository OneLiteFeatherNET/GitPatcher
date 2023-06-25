package net.onelitefeather.gitpatcher.extension

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

open class PatcherExtension(project: Project) : NamedDomainObjectContainer<PatchModule> by project.container(PatchModule::class.java) {
}