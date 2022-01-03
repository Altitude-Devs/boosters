plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

allprojects {
    group = "com.alttd.boosters"
    version = "1.0.0-BETA-SNAPSHOT"
    description = "Easily manage all boosters on the Altitude Minecraft Server Network."
}

subprojects {
    apply<JavaLibraryPlugin>()
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
        }

        withType<Javadoc> {
            options.encoding = Charsets.UTF_8.name()
        }
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
            }
        }

        configure<PublishingExtension> {
            repositories {
                maven {
                    name = "maven"
                    url = uri("https://repo.destro.xyz/snapshots/")
                    credentials(PasswordCredentials::class)
                }
                mavenCentral()
            }
        }
    }
}

dependencies {
    implementation(project(":boosters-api"))
    implementation(project(":plugin"))
    implementation(project(":velocity"))
//    implementation("net.kyori", "adventure-text-minimessage", "4.2.0-SNAPSHOT")
}

tasks {

    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")
        minimize() {
            exclude { it.moduleName == "boosters-api" }
            exclude { it.moduleName == "plugin" }
            exclude { it.moduleName == "velocity" }
        }
        listOf(
            "net.kyori.adventure.text.minimessage",
            "org.spongepowered.configurate"
        ).forEach { relocate(it, "${rootProject.name}.lib.$it") }
    }

    build {
        dependsOn(shadowJar)
    }

}

