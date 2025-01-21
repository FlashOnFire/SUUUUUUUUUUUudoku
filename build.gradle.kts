plugins {
    java
    application
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
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
    implementation("com.googlecode.lanterna:lanterna:3.1.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}