plugins {
    `maven-publish`
    id("com.github.johnrengelman.shadow")
}

dependencies {
    // API
    implementation(project(":boosters-api"))
    // Velocity
    compileOnly("com.velocitypowered:velocity-api:3.0.0")
    annotationProcessor("com.velocitypowered:velocity-api:3.0.0")
    // DiscordLink
    compileOnly("com.alttd.proxydiscordlink:ProxyDiscordLink:1.0.0-BETA-SNAPSHOT")

    implementation("mysql:mysql-connector-java:8.0.27") // mysql
    implementation("org.spongepowered", "configurate-yaml", "4.1.2")
    implementation("net.kyori", "adventure-text-minimessage", "4.1.0-SNAPSHOT") {
        exclude("net.kyori")
        exclude("net.kyori.examination")
    }
}

tasks {

    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")
        listOf(
            "net.kyori.adventure.text.minimessage",
            "org.spongepowered.configurate"
        ).forEach { relocate(it, "${rootProject.name}.lib.$it") }
    }

    build {
        dependsOn(shadowJar)
    }

}