import java.net.URI

plugins {
    kotlin("jvm") version "1.3.72"
    id("maven-publish")
}

group = "com.sultanofcardio"
version = "1.0.0"

val projectName = "iib-java-toolkit"
val include = configurations.create("include")
val integrationJars = fileTree("lib-10.0.0.21") { this.include("*.jar") }

repositories {
    mavenCentral()
}

dependencies {
    implementation(integrationJars)
    include(integrationJars)
    api("org.json:json:20190722")
    api(kotlin("stdlib-jdk8"))
    testImplementation("junit:junit:4.12")
}

val sultanofcardioUser: String by project
val sultanofcardioPassword: String by project
val sultanofcardioUrl: String by project

publishing {
    repositories {
        maven {
            name = "sultanofcardio"
            credentials {
                username = sultanofcardioUser
                password = sultanofcardioPassword
            }
            url = URI.create(sultanofcardioUrl)
        }
    }

    publications {
        create<MavenPublication>("binary") {
            artifactId = projectName
            version = project.version as String
            from(components["java"])
        }

        create<MavenPublication>("snapshot") {
            artifactId = projectName
            version = "${project.version}-SNAPSHOT"
            from(components["java"])
        }
    }
}

tasks {
    jar {
        from({
            configurations["include"].filter { it.name.endsWith(".jar") }.map { zipTree(it) }
        })
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
