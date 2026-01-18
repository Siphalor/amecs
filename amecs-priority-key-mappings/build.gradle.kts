plugins {
	id("de.siphalor.amecs.core")
	id("de.siphalor.amecs.publishing.maven")
}

val testmodSourceSet = sourceSets.named("testmod")
val testmodModifiersSourceSet = sourceSets.register("testmodModifiers") {
	compileClasspath += testmodSourceSet.get().compileClasspath
	runtimeClasspath += testmodSourceSet.get().runtimeClasspath
}

dependencies {
	compileOnly(project(":amecs-key-modifiers", configuration = "namedElements"))
	modRuntimeOnly(fabricApi.module("fabric-resource-loader-v0", mcLibs.versions.fabric.api.get()))

	"modTestmodImplementation"(fabricApi.module("fabric-key-binding-api-v1", mcLibs.versions.fabric.api.get()))

	"testmodModifiersImplementation"(sourceSets.main.map { it.output })
	"testmodModifiersImplementation"(testmodSourceSet.map { it.output })

	"testmodModifiersRuntimeOnly"(project(":amecs-key-modifiers", configuration = "namedElements"))
}
