import de.siphalor.amecs.gradle.ProjectInfoExtension

extensions.create<ProjectInfoExtension>("projectInfo").apply {
	minecraftVersionDescriptor = project.properties["minecraft.version.descriptor"] as String
	shortVersion = "${properties["version"]}"
	minecraftVersion = mcLibs.versions.minecraft.get()
	project.version = "${shortVersion}+mc${minecraftVersion}"
}

