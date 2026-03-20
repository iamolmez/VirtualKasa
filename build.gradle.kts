plugins {
    java
}

group = "com.infinitymc"
version = "1.0.0"
description = "Sanal kasa sistemi için Paper 1.20.4 plugini"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("net.milkbowl.vault:VaultAPI:1.7")
    compileOnly("me.clip:placeholderapi:2.11.5")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.jar {
    archiveBaseName.set("VirtualKasa")
    from(sourceSets.main.get().output)
    
    // Plugin.yml ve resources ekle
    from("src/main/resources")
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Copy>("copyToPlugins") {
    from(tasks.jar)
    into("plugins")
    dependsOn("jar")
}
