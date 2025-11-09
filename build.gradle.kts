import de.siphalor.jcyo.gradle.JcyoTask
import java.util.*

plugins {
	alias(libs.plugins.loom)
	java
	`maven-publish`
	alias(libs.plugins.jcyo)
	alias(libs.plugins.licenser)
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

val testmod: SourceSet by sourceSets.creating {
	compileClasspath += sourceSets.main.get().compileClasspath
	runtimeClasspath += sourceSets.main.get().runtimeClasspath
}
val compatibilityCheck: SourceSet by sourceSets.creating {
	compileClasspath += testmod.compileClasspath
	runtimeClasspath += testmod.runtimeClasspath
}

license {
	header = project.resources.text.fromFile(file("LICENSE_HEADER"))
	include("**/*.java")
}

loom {
	accessWidenerPath.set(file("src/main/resources/amecsapi.accesswidener"))

	runs {
		create("testmodClient") {
			client()
			name("Testmod Client")
			source(testmod)
		}
		create("compatibilityCheck") {
			client()
			name("Compatibility Check")
			source(compatibilityCheck)
		}
	}

	createRemapConfigurations(compatibilityCheck)
}

repositories {
	maven {
		name = "Siphalor's Maven"
		url = uri("https://maven.siphalor.de")
		mavenContent {
			includeGroupAndSubgroups("de.siphalor")
		}
	}
	maven {
		name = "BlameJared"
		url = uri("https://maven.blamejared.com")
		mavenContent {
			includeGroupAndSubgroups("com.blamejared")
		}
	}
	maven {
		name = "Modrinth"
		url = uri("https://api.modrinth.com/maven")
		mavenContent {
			includeGroupAndSubgroups("maven.modrinth")
		}
	}
	mavenLocal()
}

configurations.all {
	outgoing.capability("${project.group}:${archivesBaseName}:${shortVersion}")
	outgoing.capability("de.siphalor:amecsapi-${mcProps["minecraft.version.major"]}:${shortVersion}")
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

	"modCompileOnly"(mcLibs.bundles.compatibility)

	"testmodImplementation"(sourceSets.main.map { it.output })

	"compatibilityCheckImplementation"(sourceSets.named("testmod").map { it.output })
	"modCompatibilityCheckImplementation"(mcLibs.bundles.compatibility) {
		exclude(group = "net.fabricmc")
	}
}

tasks.processResources {
	inputs.property("version", project.version)
	val extraMixins = mcProps["mixins.extra"]?.toString()?.split(",")?.map { it.trim() } ?: listOf()
	inputs.property("extraMixins", extraMixins)

	from(sourceSets.main.get().resources.srcDirs) {
		include("fabric.mod.json")
		expand(
			"version" to project.version,
			"mixins" to extraMixins.plus("amecsapi.mixins.json").joinToString(",") { "\"$it\"" },
		)
		duplicatesStrategy = DuplicatesStrategy.INCLUDE
	}

	if (extraMixins.isNotEmpty()) {
		from(project.file("src/main/mixins")) {
			include(extraMixins)
		}
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
tasks.named<Jar>("sourcesJar") {
	dependsOn(tasks.processResources)
	from(project.layout.buildDirectory.file("resources/main/fabric.mod.json")) {
		duplicatesStrategy = DuplicatesStrategy.INCLUDE
	}
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
