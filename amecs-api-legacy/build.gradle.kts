import de.siphalor.minecraft_modding_toolkit.gradle.project_plugin.filter.JsonMergeFilterReader
import org.gradle.kotlin.dsl.filter

plugins {
	id("de.siphalor.amecs.core")
	id("de.siphalor.amecs.publishing.maven")
}

dependencies {
	implementation(project(":amecs-key-mapping-descriptions", configuration = "namedElements"))
	include(project(":amecs-key-mapping-descriptions"))
	implementation(project(":amecs-key-modifiers", configuration = "namedElements"))
	include(project(":amecs-key-modifiers"))
	implementation(project(":amecs-mouse-inputs", configuration = "namedElements"))
	include(project(":amecs-mouse-inputs"))
	implementation(project(":amecs-priority-key-mappings", configuration = "namedElements"))
	include(project(":amecs-priority-key-mappings"))
}

tasks.processResources {
	filesMatching("fabric.mod.json") {
		filter<JsonMergeFilterReader>("merge" to mapOf("depends" to mapOf(
			smcmtk.mcProps.getting("fabric.api.key_mapping_module").get() to "*"
		)))
	}
}
