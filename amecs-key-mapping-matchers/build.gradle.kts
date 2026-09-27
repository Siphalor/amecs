plugins {
	id("de.siphalor.amecs.core")
	id("de.siphalor.amecs.publishing.maven")
}

dependencies {
	modApi(fabricApi.module("fabric-api-base", mcLibs.versions.fabric.api.get()))
}
