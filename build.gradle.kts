plugins {
    id("java")
    id("maven-publish")
}

group = "dev.lechzek.packshork"
version = "STABLE"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set("packshork")
}

publishing {
    publications.create<MavenPublication>("packshork").from(components["java"])

    repositories.mavenLocal()
}
