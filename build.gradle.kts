import io.papermc.hangarpublishplugin.model.Platforms
import xyz.jpenilla.gremlin.gradle.ShadowGremlin
import xyz.jpenilla.resourcefactory.bukkit.Permission
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml.Load

plugins {
    val indraVersion = "3.1.3"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.checkstyle") version indraVersion

    id("com.gradleup.shadow") version "8.3.2"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.2.0"
    id("xyz.jpenilla.gremlin-gradle") version "0.0.6"

    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("com.modrinth.minotaur") version "2.8.7"

    id("com.github.ben-manes.versions") version "0.51.0"
}

indra {
    javaVersions {
        target(21)
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.broccol.ai/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        content { includeGroup("me.clip") }
    }
    sonatype.s01Snapshots()
    sonatype.ossSnapshots()
}

fun DependencyHandler.runtimeDownloadApi(group: String, name: String, version: String) {
    api(group, name, version)
    runtimeDownload(group, name, version)
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.21-R0.1-SNAPSHOT")
    compileOnly("org.jspecify", "jspecify", "1.0.0")

    // expansions
    compileOnly("io.github.miniplaceholders", "miniplaceholders-api", "2.2.3")
    compileOnly("me.clip", "placeholderapi", "2.11.6")

    implementation("org.incendo", "cloud-paper", "2.0.0-SNAPSHOT")
    implementation("org.incendo", "cloud-minecraft-extras", "2.0.0-SNAPSHOT")
    runtimeDownloadApi("com.google.inject", "guice", "7.0.0")
    implementation("com.google.inject.extensions", "guice-assistedinject", "7.0.0")

    implementation("org.spongepowered", "configurate-hocon", "4.1.2")
    runtimeDownloadApi("com.github.ben-manes.caffeine", "caffeine", "3.1.8")

    implementation("love.broccolai.corn", "corn-minecraft", "4.0.0-SNAPSHOT")
    implementation("love.broccolai.corn", "corn-trove", "4.0.0-SNAPSHOT")
    implementation("com.seiama", "event-api", "1.0.0-SNAPSHOT")

    // database
    implementation("com.zaxxer", "HikariCP", "5.1.0")
    runtimeDownloadApi("org.flywaydb", "flyway-core", "10.18.0")
    runtimeDownloadApi("com.h2database", "h2", "2.2.224")
    runtimeDownloadApi("org.jdbi", "jdbi3-core", "3.45.4")

    implementation("net.kyori.moonshine", "moonshine-standard", "2.0.4")
}

fun reloc(dependency: String) {
    listOf(tasks.shadowJar, tasks.writeDependencies).forEach { task ->
        task.configure {
            ShadowGremlin.relocate(this, dependency, "love.broccolai.beanstalk.libs.$dependency")
        }
    }
}

reloc("love.broccolai.corn")
reloc("org.incendo.cloud")
reloc("org.spongepowered.configurate")
reloc("com.typesafe.config")
reloc("com.seiama.event")
reloc("net.kyori.moonshine")
reloc("com.zaxxer.hikari")
reloc("io.leangen.geantyref")
reloc("org.aopalliance")
reloc("jakarta.inject")
reloc("xyz.jpenilla.gremlin")

tasks {
    runServer {
        minecraftVersion("1.20.6")

        downloadPlugins {
            github("MiniPlaceholders", "MiniPlaceholders", "2.2.4", "MiniPlaceholders-Paper-2.2.4.jar")
            hangar("PlaceholderAPI", "2.11.6")
        }
    }

    shadowJar {
        dependencies {
            exclude(dependency("org.jetbrains:annotations"))
            exclude(dependency("org.slf4j:slf4j-api"))
            exclude(dependency("org.checkerframework:checker-qual"))
            exclude(dependency("com.google.errorprone:error_prone_annotations"))
            exclude(dependency("com.h2database:h2"))
            exclude(dependency("org.flywaydb:flyway-core"))
            exclude(dependency("org.jdbi:jdbi3-core"))
            exclude(dependency("com.github.ben-manes.caffeine:caffeine"))
            exclude(dependency("com.google.inject:guice"))
            exclude(dependency("com.google.guava:guava"))
            exclude { it.moduleGroup.contains("com.fasterxml.jackson") }
            exclude { it.moduleGroup == "com.google.guava" }
        }

        archiveFileName.set(project.name + ".jar")
    }

    build {
        dependsOn(shadowJar)
    }

    writeDependencies {
        repos.set(listOf(
            "https://repo.papermc.io/repository/maven-public/"
        ))
    }
}

paperPluginYaml {
    name = "beanstalk"
    main = "love.broccolai.beanstalk.Beanstalk"
    loader = "love.broccolai.beanstalk.libs.xyz.jpenilla.gremlin.runtime.platformsupport.DefaultsPaperPluginLoader"
    apiVersion = "1.21"
    authors = listOf("broccolai")
    version = rootProject.version.toString()

    dependencies {
        server("MiniPlaceholders", Load.BEFORE, false)
        server("PlaceholderAPI", Load.BEFORE, false)
    }

    permissions {
        register("beanstalk.admin") {
            description = "Admin permissions for beanstalk"
            default = Permission.Default.OP
        }
        register("beanstalk.user") {
            description = "User permissions for beanstalk"
            default = Permission.Default.OP
        }
    }
}

val releaseNotes = providers.environmentVariable("RELEASE_NOTES")
val versions = listOf("1.21")
val shadowJar = tasks.shadowJar.flatMap { it.archiveFile }

hangarPublish.publications.register("plugin") {
    version.set(project.version as String)
    id.set("beanstalk")
    channel.set("Release")
    changelog.set(releaseNotes)
    apiKey.set(providers.environmentVariable("HANGAR_UPLOAD_KEY"))
    platforms.register(Platforms.PAPER) {
        jar.set(shadowJar)
        platformVersions.set(versions)
    }
}

modrinth {
    projectId.set("sUhzHs4l")
    versionType.set("release")
    file.set(shadowJar)
    gameVersions.set(versions)
    loaders.set(listOf("paper"))
    changelog.set(releaseNotes)
    token.set(providers.environmentVariable("MODRINTH_TOKEN"))
}
