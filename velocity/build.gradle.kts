plugins {
    `maven-publish`
}

dependencies {
    // API
    implementation(project(":boosters-api"))
    // Velocity
    compileOnly("com.velocitypowered:velocity-api:1.1.5")
    annotationProcessor("com.velocitypowered:velocity-api:1.1.5")
}