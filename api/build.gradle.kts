plugins {
    `maven-publish`
}

dependencies {
    // Galaxy
    compileOnly("com.alttd:galaxy-api:1.17.1-R0.1-SNAPSHOT")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories{
        maven {
            name = "maven"
            url = uri("http://leo:8081/")
            isAllowInsecureProtocol = true
            credentials(PasswordCredentials::class)
        }
    }
}