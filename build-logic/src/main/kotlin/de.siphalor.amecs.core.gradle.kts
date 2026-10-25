plugins {
	id("de.siphalor.amecs.base")
}

group = "de.siphalor.amecs"

tasks.processResources {
	from(layout.settingsDirectory.file("images/core-logo-48.png")) {
		into("assets/amecs-core")
		rename { "logo.png" }
	}
}

val gitTag = tasks.register<Exec>("gitTag") {
	group = "publishing"
	description = "Tags the git repo with \"<subproject-name>/<version>\""

	val tagName = "${project.name}/${project.version}"
	inputs.property("tag-name", tagName)
	shouldRunAfter(tasks.assemble)

	commandLine("git", "tag", tagName)
}

tasks.named { it == "publish" }.configureEach {
	dependsOn(gitTag)
}
