import de.siphalor.amecs.gradle.ProjectInfoExtension
import de.siphalor.jcyo.gradle.JcyoTask
import java.util.*

plugins {
	alias(libs.plugins.licenser)
	alias(libs.plugins.loom)
	alias(libs.plugins.jcyo)
	id("de.siphalor.amecs.repositories")
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
	this.accessWidenerPath = projectInfo.modId
		.map { file("src/main/resources/${it}.accesswidener") }
		.filter { it.exists() }
}

tasks.validateAccessWidener {
	enabled = false
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

tasks.jar {
	from(layout.settingsDirectory.file("LICENSE"))
}

tasks.register<Jar>("sourcesJar") {
	group = JavaBasePlugin.DOCUMENTATION_GROUP

	dependsOn(tasks.processResources)
	from(sourceSets.main.get().allJava)
	from(layout.buildDirectory.file("resources/main"))
	archiveClassifier.set("sources")
}
java.withSourcesJar()

afterEvaluate {
	val jcyoVars = mcProps.stringPropertyNames()
		.filter { it.startsWith("preprocessor.") }
		.map { it to mcProps[it] }
		.associate { (key, value) -> key.substring("preprocessor.".length) to value.toString() }

	tasks.withType<JavaCompile>() {
		val compileTask = this
		val scope = name.removeSurrounding("compile", "Java")

		val sourceSetName = if (scope.isEmpty()) { "main" } else { scope.lowercase(Locale.ROOT) }
		val sources = project.file("src/${sourceSetName}/java")
		if (sources.exists()) {
			val jcyoTask = tasks.register<JcyoTask>("jcyo${scope}") {
				inputDirectory = sources
				variables = jcyoVars
				importOrder = listOf(
					"",
					"com.mojang|net.minecraft",
					"\\#",
				)
			}

			compileTask.dependsOn(jcyoTask)

			if (scope.isEmpty()) {
				tasks.getByName("sourcesJar").dependsOn(jcyoTask)
			}
		}
	}
}
