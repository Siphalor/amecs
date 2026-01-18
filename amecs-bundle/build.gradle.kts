plugins {
	id("de.siphalor.amecs.base")
	id("de.siphalor.amecs.publishing.maven")
	id("de.siphalor.amecs.publishing.mod")
}

projectInfo {
	modId = "amecs"
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
		"fabric-key-binding-api-v1",
		"fabric-resource-loader-v0"
	)) {
		modImplementation(fabricApi.module(mod, mcLibs.versions.fabric.api.get()))
	}

	for (module in listOf(
		":amecs-api-legacy",
		":amecs-key-modifiers",
		":amecs-mouse-inputs",
		":amecs-priority-key-mappings",
	)) {
		implementation(project(module, configuration = "namedElements"))
		include(project(module))
	}
	compileOnly(project(":nmuk", configuration = "namedElements"))
}
