plugins {
	id("de.siphalor.amecs.base")
}

dependencies {
	implementation(project(":amecs-key-modifiers", configuration = "namedElements"))
	implementation(project(":amecs-priority-key-mappings", configuration = "namedElements"))

	modImplementation(fabricApi.module("fabric-key-binding-api-v1", mcLibs.versions.fabric.api.get()))
	modLocalRuntime(mcLibs.bundles.compatibility)
}

