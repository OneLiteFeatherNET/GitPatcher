import org.ajoberstar.grgit.Grgit
import java.util.*

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("org.ajoberstar.grgit") version "5.2.0"
    id("com.gradle.plugin-publish") version "1.2.0"
}


if (!File("$rootDir/.git").exists()) {
    logger.lifecycle(
        """
    **************************************************************************************
    You need to fork and clone this repository! Don't download a .zip file.
    If you need assistance, consult the GitHub docs: https://docs.github.com/get-started/quickstart/fork-a-repo
    **************************************************************************************
    """.trimIndent()
    ).also { System.exit(1) }
}

var baseVersion by extra("1.0.0")
var versionExtension by extra("")
var snapshot by extra("-SNAPSHOT")

group = "net.onelitefeather"


ext {
    versionExtension = "%s".format(Locale.ROOT, snapshot)
}

group = "net.onelitefeather.gitpatcher"



version = "%s%s".format(Locale.ROOT, baseVersion, versionExtension)

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        mavenContent {
            includeGroup("codechicken")
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("codechicken:DiffPatch:1.5.0.30")
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.9.0.202403050737-r")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

gradlePlugin {
    plugins {
        create("GitPatcher") {
            id = "net.onelitefeather.gitpatcher"
            implementationClass = "net.onelitefeather.gitpatcher.GitPatcherPlugin"
        }
    }
}

publishing {
    repositories {
    }
}