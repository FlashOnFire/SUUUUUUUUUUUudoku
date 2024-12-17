plugins {
    java
    application
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}

group = "fr.polytech.suuuuuuuuuuudoku"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass = "fr.polytech.suuuuuuuuuuudoku.Main"
    applicationDefaultJvmArgs = listOf("-ea")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}