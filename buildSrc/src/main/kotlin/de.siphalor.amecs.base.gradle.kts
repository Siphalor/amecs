import de.siphalor.amecs.gradle.ProjectInfoExtension
import de.siphalor.jcyo.gradle.JcyoTask
import de.siphalor.minecraft_modding_toolkit.gradle.project_plugin.filter.JsonMergeFilterReader
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import java.util.*

plugins {
	alias(libs.plugins.licenser)
	alias(mcLibs.plugins.smcmtk)
	alias(mcLibs.plugins.fabric.loom)
	alias(libs.plugins.jcyo)
	id("de.siphalor.amecs.repositories")
	id("de.siphalor.amecs.project-info")
}

val projectInfo = extensions.getByType<ProjectInfoExtension>()

license {
	rule(layout.settingsDirectory.file("LICENSE_HEADER"))
	include("**/*.java")
}

val testmodSourceSet = sourceSets.register("testmod") {
	compileClasspath += sourceSets.main.get().compileClasspath
	runtimeClasspath += sourceSets.main.get().runtimeClasspath
}
val compatibilityCheckSourceSet = sourceSets.register("compatibilityCheck") {
	compileClasspath += testmodSourceSet.get().compileClasspath
	runtimeClasspath += testmodSourceSet.get().runtimeClasspath
}

smcmtk {
	useMojangMappings()
	createModConfigurations(listOf(sourceSets.main.get(), testmodSourceSet.get(), compatibilityCheckSourceSet.get()))
}

tasks.validateAccessWidener {
	enabled = false
}

dependencies {
	annotationProcessor(libs.lombok)
	compileOnly(libs.lombok)
	testAnnotationProcessor(libs.lombok)
	testCompileOnly(libs.lombok)

	minecraft(mcLibs.minecraft)
	"modImplementation"(libs.fabric.loader)

	"testmodImplementation"(sourceSets.main.map { it.output })
	"compatibilityCheckImplementation"(sourceSets.named("testmod").map { it.output })

	"modCompatibilityCheckImplementation"(mcLibs.bundles.compatibility) {
		exclude(group = "net.fabricmc")
	}
}

tasks.processResources {
	inputs.property("version", project.version)
	val extraMixins = (smcmtk.mcProps.getting("mixins.extra").orNull?.split(",")?.map { it.trim() } ?: listOf())
		.filter { file("src/main/mixins/$it").exists() }

	inputs.property("extraMixins", extraMixins)

	filesMatching("fabric.mod.json") {
		filter<JsonMergeFilterReader>("merge" to mapOf("version" to project.version, "mixins" to extraMixins))
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
	tasks.withType<JavaCompile>() {
		val compileTask = this
		val scope = name.removeSurrounding("compile", "Java")

		val sourceSetName = if (scope.isEmpty()) { "main" } else { scope.lowercase(Locale.ROOT) }
		val sources = project.file("src/${sourceSetName}/java")
		if (sources.exists()) {
			val jcyoTask = tasks.register<JcyoTask>("jcyo${scope}") {
				inputDirectory = sources
				variables = smcmtk.mcProps.map { props -> props.filterKeys { it.startsWith("preprocessor.") } .mapKeys { it.key.substring("preprocessor.".length)} }
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
