plugins {
    val indraVersion = "3.1.3"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.checkstyle") version indraVersion

    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.0"
    id("xyz.jpenilla.gremlin-gradle") version "0.0.2"
    id("net.ltgt.errorprone") version "3.1.0"
}


indra {
    javaVersions {
        target(17)
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    sonatype.s01Snapshots()
}

fun DependencyHandler.runtimeDownloadApi(group: String, name: String, version: String) {
    api(group, name, version)
    runtimeDownload(group, name, version)
}

dependencies {
    errorprone("com.google.errorprone", "error_prone_core", "2.23.0")

    compileOnly("io.papermc.paper", "paper-api", "1.20.2-R0.1-SNAPSHOT")

    runtimeDownloadApi("cloud.commandframework", "cloud-paper", "1.8.4")
    runtimeDownloadApi("cloud.commandframework", "cloud-minecraft-extras", "1.8.4")
    runtimeDownloadApi("com.google.inject", "guice", "7.0.0")
    runtimeDownloadApi("com.google.inject.extensions", "guice-assistedinject", "7.0.0")

    runtimeDownloadApi("org.spongepowered", "configurate-hocon", "4.1.2")
    runtimeDownloadApi("com.github.ben-manes.caffeine", "caffeine", "3.1.0")

    // database
    runtimeDownloadApi("com.zaxxer", "HikariCP", "5.0.1")
    runtimeDownloadApi("org.flywaydb", "flyway-core", "10.0.0")
    runtimeDownloadApi("com.h2database", "h2", "2.2.224")
    runtimeDownloadApi("org.jdbi", "jdbi3-core", "3.41.3")

    runtimeDownloadApi("net.kyori.moonshine", "moonshine-standard", "2.0.4")
}

configurations.runtimeDownload {
    exclude("io.papermc.paper")
    exclude("net.kyori", "adventure-api")
    exclude("net.kyori", "adventure-text-minimessage")
    exclude("net.kyori", "adventure-text-serializer-plain")
    exclude("org.slf4j", "slf4j-api")
    exclude("org.ow2.asm")
}

tasks {
    runServer {
        minecraftVersion("1.20.2")
    }

    processResources {
        expand("version" to version)
    }

    shadowJar {
        dependencies {
            include(dependency("xyz.jpenilla:gremlin-runtime:0.0.2"))
        }

        relocate("xyz.jpenilla.gremlin", "love.broccolai.template.lib.xyz.jpenilla.gremlin")

        archiveFileName.set(project.name + ".jar")
    }

    build {
        dependsOn(shadowJar)
    }

    writeDependencies {
        repos.set(listOf(
            "https://repo.papermc.io/repository/maven-public/",
            "https://repo.maven.apache.org/maven2/",
        ))
    }
}

