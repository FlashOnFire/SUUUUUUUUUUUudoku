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
version = "1.0"

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
    implementation("io.github.spair:imgui-java-app:1.88.0")

}

tasks.test {
    useJUnitPlatform()
}

tasks.register("buildAllJars") {
    dependsOn("tuiJar", "guiSwingJar", "guiImGUIJar")
}

tasks.register<JavaExec>("runTui") {
    mainClass.set("fr.polytech.suuuuuuuuuuudoku.graphics.Tui")
    classpath = sourceSets.main.get().runtimeClasspath + files("src/test/resources")
    args = listOf()
}

tasks.register<JavaExec>("runSwing") {
    mainClass.set("fr.polytech.suuuuuuuuuuudoku.graphics.SudokuFrame")
    classpath = sourceSets.main.get().runtimeClasspath + files("src/test/resources")
    args = listOf()
}

tasks.register<JavaExec>("runImGUI") {
    mainClass.set("fr.polytech.suuuuuuuuuuudoku.graphics.ImGUIFrame")
    classpath = sourceSets.main.get().runtimeClasspath + files("src/test/resources")
    args = listOf()
}

tasks.register<Jar>("tuiJar") {
    archiveBaseName.set("tui")
    manifest {
        attributes["Main-Class"] = "fr.polytech.suuuuuuuuuuudoku.graphics.Tui"
    }
    from(sourceSets.main.get().output)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    from("src/main/resources")
    from("src/test/resources")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Jar>("guiSwingJar") {
    archiveBaseName.set("swing")
    manifest {
        attributes["Main-Class"] = "fr.polytech.suuuuuuuuuuudoku.graphics.SudokuFrame"
    }
    from(sourceSets.main.get().output)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    from("src/main/resources")
    from("src/test/resources")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Jar>("guiImGUIJar") {
    archiveBaseName.set("imGUI")
    manifest {
        attributes["Main-Class"] = "fr.polytech.suuuuuuuuuuudoku.graphics.ImGUIFrame"
    }
    from(sourceSets.main.get().output)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    from("src/main/resources")
    from("src/test/resources")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<JavaExec>("run") {
    enabled = false
}