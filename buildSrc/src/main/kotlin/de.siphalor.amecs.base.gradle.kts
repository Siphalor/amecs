import de.siphalor.amecs.gradle.ProjectInfoExtension
import de.siphalor.jcyo.gradle.JcyoTask
import java.util.*

plugins {
	alias(libs.plugins.licenser)
	alias(libs.plugins.loom)
	alias(libs.plugins.jcyo)
	id("de.siphalor.amecs.project-info")
}

group = "de.siphalor.amecs"

val projectInfo = extensions.getByType<ProjectInfoExtension>()
val mcProps = Properties().apply {
	val propFile = project.layout.settingsDirectory.file("gradle/mc-${projectInfo.minecraftVersionDescriptor.get()}/gradle.properties")
	load(propFile.asFile.inputStream())
}

license {
	rule(layout.settingsDirectory.file("LICENSE_HEADER"))
	include("**/*.java")
}

loom {
	val accessWidenerFile = file("src/main/resources/${project.name}.accesswidener")
	if (accessWidenerFile.exists()) {
		this.accessWidenerPath = accessWidenerFile
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
	maven {
		name = "BlameJared"
		url = uri("https://maven.blamejared.com")
		mavenContent {
			includeGroupAndSubgroups("com.blamejared")
		}
	}
	mavenLocal()
}

val testmodSourceSet = sourceSets.register("testmod") {
	compileClasspath += sourceSets.main.get().compileClasspath
	runtimeClasspath += sourceSets.main.get().runtimeClasspath
}
val compatibilityCheckSourceSet = sourceSets.register("compatibilityCheck") {
	compileClasspath += testmodSourceSet.get().compileClasspath
	runtimeClasspath += testmodSourceSet.get().runtimeClasspath
}

loom.createRemapConfigurations(testmodSourceSet.get())
loom.createRemapConfigurations(compatibilityCheckSourceSet.get())

dependencies {
	annotationProcessor(libs.lombok)
	compileOnly(libs.lombok)
	testAnnotationProcessor(libs.lombok)
	testCompileOnly(libs.lombok)

	minecraft(mcLibs.minecraft)
	mappings(loom.officialMojangMappings())
	"modImplementation"(libs.fabric.loader)

	"testmodImplementation"(sourceSets.main.map { it.output })
	"compatibilityCheckImplementation"(sourceSets.named("testmod").map { it.output })

	"modCompatibilityCheckImplementation"(mcLibs.bundles.compatibility) {
		exclude(group = "net.fabricmc")
	}
}

tasks.processResources {
	inputs.property("version", project.version)
	val extraMixins = (mcProps["mixins.extra"]?.toString()?.split(",")?.map { it.trim() } ?: listOf())
		.filter { file("src/main/mixins/$it").exists() }

	inputs.property("extraMixins", extraMixins)

	filesMatching("fabric.mod.json") {
		expand(
			"version" to project.version,
			"extra_mixins" to if (extraMixins.isEmpty()) "" else "," + extraMixins.joinToString(",") { "\"$it\"" }
		)
	}

	from(layout.settingsDirectory.file("images/core-logo-48.png")) {
		into("assets/amecs-core")
		rename { "logo.png" }
	}

	if (extraMixins.isNotEmpty()) {
		from(file("src/main/mixins")) {
			include(extraMixins)
		}
	}
}

java {
	sourceCompatibility = JavaVersion.toVersion(mcLibs.versions.java.get())
	targetCompatibility = JavaVersion.toVersion(mcLibs.versions.java.get())
}

val jcyoVars = mcProps.stringPropertyNames()
	.filter { it.startsWith("preprocessor.") }
	.map { it to mcProps[it] }
	.associate { (key, value) -> key.substring("preprocessor.".length) to value.toString() }
val jcyo = tasks.register<JcyoTask>("jcyo") {
	inputDirectory = file("src/main/java")
	variables = jcyoVars
}

tasks.compileJava {
	dependsOn(jcyo)
}

tasks.jar {
	from(layout.settingsDirectory.file("LICENSE"))
}

tasks.register<Jar>("sourcesJar") {
	group = JavaBasePlugin.DOCUMENTATION_GROUP

	dependsOn(jcyo, tasks.processResources)
	from(sourceSets.main.get().allJava)
	from(layout.buildDirectory.file("resources/main"))
	archiveClassifier.set("sources")
}
java.withSourcesJar()
