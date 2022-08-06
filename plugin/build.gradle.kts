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
    implementation("net.kyori", "adventure-text-minimessage", "4.2.0-SNAPSHOT") {
        exclude("net.kyori")
        exclude("net.kyori.examination")
    }
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
    commands {
        create("boosterlist") {
            description = "Show a list of boosters currently active on the server."
            usage = "/boosterlist"
            permission = "command.booster"
        }
    }
}