import io.papermc.hangarpublishplugin.model.Platforms
import xyz.jpenilla.gremlin.gradle.ShadowGremlin
import xyz.jpenilla.resourcefactory.bukkit.Permission
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml.Load

plugins {
    val indraVersion = "4.0.0"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.checkstyle") version indraVersion

    id("com.gradleup.shadow") version "9.4.0"
    id("xyz.jpenilla.run-paper") version "3.1.0"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.1"
    id("xyz.jpenilla.gremlin-gradle") version "0.0.9"

    id("io.papermc.hangar-publish-plugin") version "0.1.4"
    id("com.modrinth.minotaur") version "2.8.9"

    id("io.github.ben-manes.versions") version "0.61.0"
}

indra {
    javaVersions {
        target(25)
        minimumToolchain(25)
    }
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.broccol.ai/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        content { includeGroup("me.clip") }
    }
}

fun DependencyHandler.runtimeDownloadApi(dependency: String) {
    api(dependency)
    runtimeDownload(dependency)
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.74-stable")
    compileOnly("org.jspecify:jspecify:1.0.0")

    // expansions
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:3.2.0")
    compileOnly("me.clip:placeholderapi:2.12.3")

    implementation("org.incendo:cloud-paper:2.1.0-SNAPSHOT")
    implementation("org.incendo:cloud-minecraft-extras:2.1.0-SNAPSHOT")
    runtimeDownloadApi("com.google.inject:guice:7.0.0")
    implementation("com.google.inject.extensions:guice-assistedinject:7.0.0")

    implementation("org.spongepowered:configurate-hocon:4.2.0")
    runtimeDownloadApi("com.github.ben-manes.caffeine:caffeine:3.2.0")

    implementation("love.broccolai.corn:corn-minecraft:4.1.0-SNAPSHOT")
    implementation("love.broccolai.corn:corn-trove:4.1.0-SNAPSHOT")
    implementation("com.sasorio:event-api:1.0.0")

    // database
    implementation("com.zaxxer:HikariCP:6.3.0")
    runtimeDownloadApi("org.flywaydb:flyway-core:13.2.0")
    runtimeDownloadApi("com.h2database:h2:2.3.232")
    runtimeDownloadApi("org.jdbi:jdbi3-core:3.49.0")

    implementation("net.kyori.moonshine:moonshine-standard:2.0.4")
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
reloc("net.kyori.option")
reloc("com.typesafe.config")
reloc("com.sasorio.event")
reloc("net.kyori.moonshine")
reloc("com.zaxxer.hikari")
reloc("io.leangen.geantyref")
reloc("org.aopalliance")
reloc("jakarta.inject")
reloc("xyz.jpenilla.gremlin")
reloc("org.antlr")
reloc("org.jspecify")
reloc("com.google.inject.assistedinject")

tasks {
    runServer {
        minecraftVersion("26.1.2")

        downloadPlugins {
            github("MiniPlaceholders", "MiniPlaceholders", "3.2.0", "MiniPlaceholders-Paper-3.2.0.jar")
            hangar("PlaceholderAPI", "2.12.3")
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
    bootstrapper = "love.broccolai.beanstalk.BeanstalkBootstrap"
    loader = "love.broccolai.beanstalk.libs.xyz.jpenilla.gremlin.runtime.platformsupport.DefaultsPaperPluginLoader"
    apiVersion = "26.1.2"
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
val versions = listOf("26.1.2")
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
