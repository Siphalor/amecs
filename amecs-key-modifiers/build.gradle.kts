plugins {
	id("de.siphalor.amecs.core")
}

dependencies {
	for (mod in listOf(
		"fabric-api-base",
		"fabric-key-binding-api-v1",
	)) {
		modImplementation(fabricApi.module(mod, mcLibs.versions.fabric.api.get()))
	}
	modRuntimeOnly(fabricApi.module("fabric-resource-loader-v0", mcLibs.versions.fabric.api.get()))

	modCompileOnly(mcLibs.bundles.compatibility)

	include(project(":amecs-api-legacy-dummy"))
	runtimeOnly(project(":amecs-api-legacy-dummy", configuration = "namedElements"))
}
