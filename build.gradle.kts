plugins {
    java
    id("com.gradleup.shadow") version "9.2.2"
}

val id = project.property("id") as String
val extensionName = project.property("name") as String
val author = project.property("author") as String
val version = project.version as String
val geyserApiVersion = "2.9.0"

val bedrockProtocolVersion = "3.0.0.Beta10-20251014.180344-2"
val undertowVersion = "2.3.20.Final"

repositories {
    mavenLocal()

    // Repo for the Geyser API artifact
    maven("https://repo.opencollab.dev/main/")

    // Add other repositories here
    mavenCentral()
}

dependencies {
    // Geyser API - needed for all extensions
    compileOnly("org.geysermc.geyser:api:$geyserApiVersion-SNAPSHOT")

    // Bedrock protocol libraries
    compileOnly("org.cloudburstmc.protocol:common:$bedrockProtocolVersion")
    compileOnly("org.cloudburstmc.protocol:bedrock-codec:$bedrockProtocolVersion")
    compileOnly("org.cloudburstmc.protocol:bedrock-connection:$bedrockProtocolVersion")

    // Gson for JSON handling
    compileOnly("com.google.code.gson:gson:2.3.1")

    // Undertow for web server capabilities
    implementation("io.undertow:undertow-core:$undertowVersion")
    implementation("io.undertow:undertow-websockets-jsr:${undertowVersion}")
}

tasks.shadowJar {
    archiveClassifier.set("") // Replace the main jar
}

// Java currently requires Java 17 or higher, so extensions should also target it
java {
    targetCompatibility = JavaVersion.VERSION_17
    sourceCompatibility = JavaVersion.VERSION_17
}

afterEvaluate {
    val idRegex = Regex("[a-z][a-z0-9-_]{0,63}")
    if (idRegex.matches(id).not()) {
        throw IllegalArgumentException("Invalid extension id $id! Must only contain lowercase letters, " +
                "and cannot start with a number.")
    }

    val nameRegex = Regex("^[A-Za-z_.-]+$")
    if (nameRegex.matches(extensionName).not()) {
        throw IllegalArgumentException("Invalid extension name $extensionName! Must fit regex: ${nameRegex.pattern})")
    }
}

tasks {
    // This automatically fills in the extension.yml file.
    processResources {
        filesMatching("extension.yml") {
            expand(
                "id" to id,
                "name" to extensionName,
                "api" to geyserApiVersion,
                "version" to version,
                "author" to author
            )
        }
    }
}