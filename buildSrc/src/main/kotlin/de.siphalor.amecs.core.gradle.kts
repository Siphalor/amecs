plugins {
	id("de.siphalor.amecs.base")
}

tasks.processResources {
	from(layout.settingsDirectory.file("images/core-logo-48.png")) {
		into("assets/amecs-core")
		rename { "logo.png" }
	}
}
