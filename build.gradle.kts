plugins {
    `java-library`
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