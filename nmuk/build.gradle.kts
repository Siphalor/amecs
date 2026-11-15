plugins {
	id("de.siphalor.amecs.base")
	id("de.siphalor.amecs.publishing.maven")
	id("de.siphalor.amecs.publishing.mod")
}

group = "de.siphalor.nmuk"

smcmtk {
	useAccessWidener(project.layout.projectDirectory.file("src/main/resources/nmuk.accesswidener"))
}

loom {
	runs {
		create("testmodClient") {
			client()
			displayName = "Testmod Client"
			sourceSet = "testmod"
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

	compileOnly(project(":amecs-key-mapping-matchers", configuration = "namedElements"))
	compileOnly(project(":amecs-key-modifiers", configuration = "namedElements"))
	testmodImplementation(project(":amecs-key-mapping-matchers", configuration = "namedElements"))
	testmodImplementation(project(":amecs-key-modifiers", configuration = "namedElements"))
}

tasks.processResources {
	from(layout.settingsDirectory.file("images/nmuk-logo-128.png")) {
		into("assets/nmuk")
		rename { "logo.png" }
	}
}

license {
	projectCreationYear = 2021
}
