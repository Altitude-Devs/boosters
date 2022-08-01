plugins {
    `maven-publish`
    id("com.github.johnrengelman.shadow")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

dependencies {
    // API
    implementation(project(":boosters-api"))
    // Galaxy
    compileOnly("com.alttd:Galaxy-API:1.18.1-R0.1-SNAPSHOT")
    // MyPet
    compileOnly("de.keyle:mypet:3.12-SNAPSHOT")
    // mcMMO
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.1.206")
    compileOnly("com.sk89q.worldguard:worldguard-core:7.0.4") {
        exclude("com.google.code.findbugs")
    }

    //Jackson (json)
    compileOnly("com.fasterxml.jackson.core:jackson-core:2.8.8")
    compileOnly("com.fasterxml.jackson.core:jackson-annotations:2.8.8")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.8.8")
}

tasks {

    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")
    }

    build {
        dependsOn(shadowJar)
    }

}

bukkit {
    name = rootProject.name
    main = "$group.BoostersPlugin"
    apiVersion = "1.18"
    authors = listOf("destro174")
    softDepend = listOf("MyPet", "mcMMO")
}