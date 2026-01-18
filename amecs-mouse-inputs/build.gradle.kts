plugins {
	id("de.siphalor.amecs.core")
	id("de.siphalor.amecs.publishing.maven")
}

val testmodModifiersSourceSet = sourceSets.register("testmodModifiers") {
	compileClasspath += sourceSets.main.get().compileClasspath
	runtimeClasspath += sourceSets.main.get().runtimeClasspath
}

loom {
	accessWidenerPath.set(file("src/main/resources/amecs_mouse_inputs.accesswidener"))

	createRemapConfigurations(testmodModifiersSourceSet.get())
}

dependencies {
	compileOnly(project(":amecs-priority-key-mappings", configuration = "namedElements"))
	modRuntimeOnly(fabricApi.module("fabric-resource-loader-v0", mcLibs.versions.fabric.api.get()))

	"testmodModifiersImplementation"(sourceSets.main.map { it.output })
	"testmodModifiersRuntimeOnly"(project(":amecs-key-modifiers", configuration = "namedElements"))
}
