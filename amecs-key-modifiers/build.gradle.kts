plugins {
	id("de.siphalor.amecs.core")
	id("de.siphalor.amecs.publishing.maven")
}

dependencies {
	for (mod in listOf(
		"fabric-api-base",
		smcmtk.mcProps.getting("fabric.api.key_mapping_module").get(),
	)) {
		modImplementation(fabricApi.module(mod, mcLibs.versions.fabric.api.get()))
	}
	modRuntimeOnly(fabricApi.module("fabric-resource-loader-v0", mcLibs.versions.fabric.api.get()))

	modCompileOnly(mcLibs.bundles.compatibility)

	include(project(":amecs-api-legacy-dummy"))
	localRuntime(project(":amecs-api-legacy-dummy", configuration = "namedElements"))
}
