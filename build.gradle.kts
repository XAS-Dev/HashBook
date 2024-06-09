plugins {
    id("java")
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val group = "xyz.xasmc"
val version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "CodeMC"
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.4.2")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.9.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.17.0") {}
    implementation(kotlin("stdlib"))
}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.jar {
    archiveVersion.set(version)
    archiveClassifier.set("bare")
}

tasks.shadowJar {
    archiveVersion.set(version)
    archiveClassifier.set("")
    relocate("org.jetbrains", "xyz.xasmc.hashbook.lib.jetbrains")
    relocate("org.intellij", "xyz.xasmc.hashbook.lib.intellij")
    relocate("kotlin", "xyz.xasmc.hashbook.lib.kotlin")
    mergeServiceFiles()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

kotlin {
    jvmToolchain {
        this.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}