plugins {
    `maven-publish`
}

dependencies {
    compileOnly("com.alttd:Galaxy-API:1.19.2-R0.1-SNAPSHOT")
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