plugins {
    `maven-publish`
}

dependencies {
    // API
    implementation(project(":boosters-api"))
    // Velocity
    compileOnly("com.velocitypowered:velocity-api:3.0.0")
    annotationProcessor("com.velocitypowered:velocity-api:3.0.0")
    // DiscordLink
    compileOnly("com.alttd.proxydiscordlink:ProxyDiscordLink:1.0.0-BETA-SNAPSHOT")
}