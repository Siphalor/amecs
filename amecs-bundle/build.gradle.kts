import de.siphalor.minecraft_modding_toolkit.gradle.project_plugin.filter.JsonMergeFilterReader

plugins {
	id("de.siphalor.amecs.base")
	id("de.siphalor.amecs.publishing.maven")
	id("de.siphalor.amecs.publishing.mod")
}

group = "de.siphalor.amecs"

projectInfo {
	modId = "amecs"
}

smcmtk {
	useAccessWidener(project.layout.projectDirectory.file("src/main/resources/amecs.accesswidener"))
}

loom {
	runs {
		create("testmodClient") {
			client()
			name("Testmod Client")
			source(sourceSets.getByName("testmod"))
		}
	}
}

dependencies {
	for (mod in listOf(
		"fabric-api-base",
		smcmtk.mcProps.getting("fabric.api.key_mapping_module").get(),
		"fabric-resource-loader-v0"
	)) {
		modImplementation(fabricApi.module(mod, mcLibs.versions.fabric.api.get()))
	}

	for (module in listOf(
		":amecs-api-legacy",
		":amecs-key-modifiers",
		":amecs-priority-key-mappings",
	)) {
		implementation(project(module, configuration = "namedElements"))
	}

	// The legacy module right now embeds all the other modules
	include(project(":amecs-api-legacy"))

	compileOnly(project(":nmuk", configuration = "namedElements"))
}

tasks.processResources {
	from(layout.settingsDirectory.file("images/amecs-logo-128.png")) {
		into("assets/amecs")
		rename { "logo.png" }
	}
	filesMatching("fabric.mod.json") {
		filter<JsonMergeFilterReader>("merge" to mapOf("depends" to mapOf(
			smcmtk.mcProps.getting("fabric.api.key_mapping_module").get() to "*"
		)))
	}
}
