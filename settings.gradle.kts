rootProject.name = "Boosters"

include(":plugin")
include(":velocity")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        // Altitude - Galaxy
        maven {
            name = "maven"
            url = uri("http://leo:8081/")
            isAllowInsecureProtocol = true
            //credentials(PasswordCredentials::class)
        }
        // Velocity
        maven("https://nexus.velocitypowered.com/repository/maven-public/")
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
