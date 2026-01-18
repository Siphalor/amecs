import de.siphalor.amecs.gradle.ProjectInfoExtension
import java.io.StringReader
import java.util.Properties

val mcVersionDescriptor = providers.gradleProperty("minecraft.version.descriptor")
val mcProps = mcVersionDescriptor.flatMap { descriptor ->
	providers.fileContents(project.layout.settingsDirectory.file("gradle/mc-${descriptor}/gradle.properties")).asText
}.map { propsFile -> Properties().apply { load(StringReader(propsFile))} }

val projectInfo = extensions.create<ProjectInfoExtension>("projectInfo").apply {
	shortVersion = "${properties["version"]}"

	minecraftVersionDescriptor = mcVersionDescriptor
	minecraftVersion = mcLibs.versions.minecraft.get()
	minecraftVersionTitle = mcProps.map { it["minecraft.version.title"] as String }
	supportedMinecraftVersions = mcProps.map { (it["mc.version.supported"] as String).split(",").map { it.trim() } }
}

project.version = "${projectInfo.shortVersion.get()}+mc${projectInfo.minecraftVersion.get()}"

