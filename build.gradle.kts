plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "de.mickymaus209.msg"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") //Spigot
    maven("https://repo.extendedclip.com/releases") // PlaceholderAPI repository
    maven("https://repo.md-5.net/content/repositories/releases/") //BungeeCord API
    maven("https://libraries.minecraft.net/") //Minecraft libraries required for BungeeCord
    mavenCentral()

}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("net.md-5:bungeecord-api:1.21-R0.2")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.bstats:bstats-bukkit:3.0.0")
    implementation("org.bstats:bstats-bungeecord:3.0.0")
    implementation("com.zaxxer:HikariCP:3.4.5")
    implementation("com.mysql:mysql-connector-j:8.4.0")
}

java{
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    relocate("org.bstats", "$group.libs.bstats")
    relocate("com.google.gson", "$group.libs.gson")
    relocate("com.zaxxer.hikari", "$group.libs.hikari")
    relocate("com.mysql", "$group.libs.mysql")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}


