import de.siphalor.amecs.gradle.ProjectInfoExtension
import org.gradle.kotlin.dsl.getByType

plugins {
	alias(libs.plugins.loom)
	alias(libs.plugins.modPublisher)
	id("de.siphalor.amecs.project-info")
}

val projectInfo = extensions.getByType<ProjectInfoExtension>()

publisher {
	apiKeys {
		project.findProperty("modrinth.token")?.let { modrinth(it as String) }
		project.findProperty("curseforge.token")?.let { curseforge(it as String) }
		project.findProperty("github.token")?.let { github(it as String) }
	}

	curseID = providers.gradleProperty("curseforge.id")
	modrinthID = providers.gradleProperty("modrinth.id")

	artifact.set(tasks.remapJar)

	projectVersion = projectInfo.shortVersion
	versionType = providers.gradleProperty("version.type")
	loaders = listOf("fabric")
	curseEnvironment = "client"

	gameVersions = projectInfo.supportedMinecraftVersions

	displayName = projectInfo.minecraftVersionTitle.zip(projectInfo.shortVersion)
		{ mcTitle, version -> "[$mcTitle] $version" }
	// TODO: changelog

	github {
		repo("Siphalor/amecs")
		tag("${project.name}/${projectInfo.shortVersion.get()}")
		displayName(projectInfo.shortVersion.get())
		createTag(true)
		createRelease(true)
	}
}
