plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

allprojects {
    val build = System.getenv("BUILD_NUMBER") ?: "SNAPSHOT"
    group = "com.alttd.boosters"
    version = "1.0.0-BETA-$build"
    description = "Easily manage all boosters on the Altitude Minecraft Server Network."
}

subprojects {
    apply<JavaLibraryPlugin>()
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(16))
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
                    name = "nexus"
                    url = uri("http://$name:8081/snapshots")
                    isAllowInsecureProtocol = true
                    credentials(PasswordCredentials::class)
                }
            }
        }
    }
}

dependencies {
    implementation(project(":boosters-api"))
    implementation(project(":plugin"))
    implementation(project(":velocity"))
    implementation("net.kyori", "adventure-text-minimessage", "4.1.0-SNAPSHOT")
}

tasks {

    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")
        listOf(
            "net.kyori.adventure.text.minimessage"
        ).forEach { relocate(it, "${rootProject.group}.lib.$it") }
    }

    build {
        dependsOn(shadowJar)
    }

}