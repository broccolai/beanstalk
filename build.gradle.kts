import io.papermc.hangarpublishplugin.model.Platforms
import xyz.jpenilla.resourcefactory.bukkit.Permission

plugins {
    val indraVersion = "3.1.3"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.checkstyle") version indraVersion

    id("com.gradleup.shadow") version "8.3.2"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.2.0"

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
    sonatype.s01Snapshots()
    sonatype.ossSnapshots()
}

configurations {
    create("relocateDeps") {
        isCanBeResolved = true
        isCanBeConsumed = false

        extendsFrom(configurations.implementation.get())
    }
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.21-R0.1-SNAPSHOT")
    compileOnly("org.jspecify", "jspecify", "1.0.0")

    implementation("org.incendo", "cloud-paper", "2.0.0-SNAPSHOT")
    implementation("org.incendo", "cloud-minecraft-extras", "2.0.0-SNAPSHOT")
    implementation("com.google.inject", "guice", "7.0.0") {
        exclude("com.google.guava")
    }
    implementation("com.google.inject.extensions", "guice-assistedinject", "7.0.0")

    implementation("org.spongepowered", "configurate-hocon", "4.1.2")
    implementation("com.github.ben-manes.caffeine", "caffeine", "3.1.8")

    implementation("love.broccolai.corn", "corn-minecraft", "4.0.0-SNAPSHOT")
    implementation("love.broccolai.corn", "corn-trove", "4.0.0-SNAPSHOT")
    implementation("com.seiama", "event-api", "1.0.0-SNAPSHOT")

    // database
    implementation("com.zaxxer", "HikariCP", "5.1.0")
    implementation("org.flywaydb", "flyway-core", "10.18.0")
    implementation("com.h2database", "h2", "2.2.224")
    implementation("org.jdbi", "jdbi3-core", "3.45.4")

    implementation("net.kyori.moonshine", "moonshine-standard", "2.0.4")
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }

    shadowJar {
        dependencies {
            fun relocate(dependency: String) {
                relocate(dependency, "love.broccolai.beanstalk.libs.$dependency")
            }

            relocate("love.broccolai.corn")
            relocate("com.github.benmanes.caffeine")
            relocate("org.incendo.cloud")
            relocate("org.spongepowered.configurate")
            relocate("com.typesafe.config")
            relocate("com.seiama.event")
            relocate("net.kyori.moonshine")
            relocate("com.zaxxer.hikari")
            relocate("io.leangen.geantyref")
            relocate("org.jdbi")
            // guice
            relocate("com.google.inject")
            relocate("org.aopalliance")
            relocate("jakarta.inject")

            exclude(dependency("org.slf4j:slf4j-api"))
            exclude(dependency("org.checkerframework:checker-qual"))
            exclude(dependency("com.google.errorprone:error_prone_annotations"))
            exclude { it.moduleGroup == "com.google.guava" }
        }

        archiveFileName.set(project.name + ".jar")
    }

    build {
        dependsOn(shadowJar)
    }
}

paperPluginYaml {
    name = "beanstalk"
    main = "love.broccolai.beanstalk.Beanstalk"
    apiVersion = "1.21"
    authors = listOf("broccolai")
    version = rootProject.version.toString()

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
