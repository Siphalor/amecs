import de.siphalor.jcyo.gradle.JcyoTask
import java.util.*

plugins {
	alias(libs.plugins.loom)
	java
	`maven-publish`
	alias(libs.plugins.jcyo)
	alias(libs.plugins.licenser)

	alias(libs.plugins.modPublisher)
}

val minecraftVersionDescriptor = project.properties["minecraft.version.descriptor"] as String
val mcProps = Properties().apply {
	val propFile = project.layout.settingsDirectory.file("gradle/mc-${minecraftVersionDescriptor}/gradle.properties")
	load(propFile.asFile.inputStream())
}

group = "de.siphalor.${project.name}"
val archivesBaseName = "${project.name}-mc${minecraftVersionDescriptor}"
val shortVersion = "${properties["version"]}"
version = "${shortVersion}+mc${mcLibs.versions.minecraft.get()}"

sourceSets {
	create("testmod") {
		compileClasspath += sourceSets.main.get().compileClasspath
		runtimeClasspath += sourceSets.main.get().runtimeClasspath
	}
}

license {
	header = project.resources.text.fromFile(file("LICENSE_HEADER"))
	include("**/*.java")
}

loom {
	accessWidenerPath.set(file("src/main/resources/nmuk.accesswidener"))

	runs {
		create("testmodClient") {
			client()
			name("Testmod Client")
			source(sourceSets.getByName("testmod"))
		}
	}
}

repositories {
	maven {
		name = "Siphalor's Maven"
		url = uri("https://maven.siphalor.de")
		mavenContent {
			includeGroupAndSubgroups("de.siphalor")
		}
	}
	mavenLocal()
}

dependencies {
	annotationProcessor(libs.lombok)
	compileOnly(libs.lombok)
	testAnnotationProcessor(libs.lombok)
	testCompileOnly(libs.lombok)

	minecraft(mcLibs.minecraft)
	mappings(loom.officialMojangMappings())
	"modImplementation"(libs.fabric.loader)

	for (mod in listOf(
		"fabric-api-base",
		"fabric-key-binding-api-v1",
		"fabric-resource-loader-v0"
	)) {
		"modImplementation"(fabricApi.module(mod, mcLibs.versions.fabric.api.get()))
	}

	modImplementation(mcLibs.amecsApi)
	include(mcLibs.amecsApi)

	"testmodImplementation"(sourceSets.main.map { it.output })
}


tasks.processResources {
	inputs.property("version", project.version)

	exclude("*.svg")
	from(sourceSets.main.get().resources.srcDirs) {
		include("fabric.mod.json")
		expand("version" to project.version)
		duplicatesStrategy = DuplicatesStrategy.INCLUDE
	}
}

java {
	sourceCompatibility = JavaVersion.toVersion(mcLibs.versions.java.get())
	targetCompatibility = JavaVersion.toVersion(mcLibs.versions.java.get())
	withSourcesJar()
}

val jcyoVars = mcProps.stringPropertyNames()
	.filter { it.startsWith("preprocessor.") }
	.map { it to mcProps[it] }
	.associate { (key, value) -> key.substring("preprocessor.".length) to value }
val jcyo = tasks.register<JcyoTask>("jcyo") {
	inputDirectory = file("src/main/java")
	variables = jcyoVars
}
val testmodJcyo = tasks.register<JcyoTask>("testmodJcyo") {
	inputDirectory = file("src/testmod/java")
	variables = jcyoVars
}

tasks.compileJava {
	dependsOn(jcyo)
}
tasks.named("compileTestmodJava") {
	dependsOn(testmodJcyo)
}

tasks.jar {
	from(file("LICENSE"))
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = archivesBaseName
			version = shortVersion

			from(components["java"])
		}
	}

	repositories {
		if (project.hasProperty("siphalor.maven.user")) {
			maven {
				name = "Siphalor"
				url = uri("https://maven.siphalor.de/upload.php")
				credentials {
					username = project.property("siphalor.maven.user") as String
					password = project.property("siphalor.maven.password") as String
				}
			}
		}
	}
}

publisher {
	apiKeys {
		project.findProperty("modrinth.token")?.let { modrinth(it as String) }
		project.findProperty("curseforge.token")?.let { modrinth(it as String) }
		project.findProperty("github.token")?.let { github(it as String) }
	}

	debug = true

	curseID = "438172"
	modrinthID = "YCcdA1Lp"

	artifact.set(tasks.remapJar)

	projectVersion = project.version as String
	versionType = project.property("version.type") as String
	loaders = listOf("fabric")
	curseEnvironment = "client"

	gameVersions = (mcProps["mc.version.supported"] as String).split(", ")

	displayName = "[${mcProps["mc.version.title"]}] $shortVersion"
	changelog.set(providers.exec {
		commandLine("git", "log", "-1", "--format=format:##%x20%s%n%n%b%nRelease%x20by%x20%an", "--grep", "Version")
	}.standardOutput.asText.map { it.trim() })
}
