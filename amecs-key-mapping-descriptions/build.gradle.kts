plugins {
	id("de.siphalor.amecs.core")
	id("de.siphalor.amecs.publishing.maven")
}

dependencies {
	testmodRuntimeOnly(fabricApi.module("fabric-resource-loader-v0", mcLibs.versions.fabric.api.get()))

	modCompileOnly(mcLibs.bundles.compatibility)
}
