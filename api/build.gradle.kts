plugins {
    `maven-publish`
}

dependencies {
    compileOnly("com.alttd:Galaxy-API:1.18.1-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-text-minimessage:4.2.0-SNAPSHOT") // Minimessage
    compileOnly("org.spongepowered:configurate-yaml:4.1.2") // Configurate
    compileOnly("net.luckperms:api:5.3") // Luckperms

    //Jackson (json)
    compileOnly("com.fasterxml.jackson.core:jackson-core:2.8.8")
    compileOnly("com.fasterxml.jackson.core:jackson-annotations:2.8.8")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.8.8")
}

publishing {
    repositories{
        maven {
            name = "maven"
            url = uri("https://repo.destro.xyz/snapshots")
            credentials(PasswordCredentials::class)
        }
    }
}