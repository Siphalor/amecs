plugins {
	id("de.siphalor.amecs.core")
}

dependencies {
	testmodRuntimeOnly(fabricApi.module("fabric-resource-loader-v0", mcLibs.versions.fabric.api.get()))

	modCompileOnly(mcLibs.bundles.compatibility)
}
