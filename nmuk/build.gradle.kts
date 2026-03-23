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
			name("Testmod Client")
			source(sourceSets.getByName("testmod"))
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

	compileOnly(project(":amecs-key-modifiers"))
}

tasks.processResources {
	from(layout.settingsDirectory.file("images/nmuk-logo-128.png")) {
		into("assets/nmuk")
		rename { "logo.png" }
	}
}
