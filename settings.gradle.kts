rootProject.name = "Boosters"

include(":plugin")
include(":velocity")

dependencyResolutionManagement {
    repositories {
//        mavenLocal()
        mavenCentral()
        maven("https://repo.destro.xyz/snapshots") // Altitude - Galaxy
        maven("https://oss.sonatype.org/content/groups/public/") // Adventure
        maven("https://nexus.velocitypowered.com/repository/maven-public/") // Velocity
        maven("https://repo.spongepowered.org/maven") // Configurate
        maven("https://nexus.neetgames.com/repository/maven-releases/") // mcMMO
        maven("https://maven.enginehub.org/repo/") // worldguard
        maven { // mypet
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/MyPetORG/MyPet")
            credentials(PasswordCredentials::class)
        }

    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

// Unsure if API is required in this project
setupSubproject("boosters-api") {
    projectDir = file("api")
}

inline fun setupSubproject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}
