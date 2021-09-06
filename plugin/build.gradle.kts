plugins {
    `maven-publish`
}

dependencies {
    // API
    implementation(project(":boosters-api"))
    // Galaxy
    compileOnly("com.alttd:galaxy-api:1.17.1-R0.1-SNAPSHOT")
}