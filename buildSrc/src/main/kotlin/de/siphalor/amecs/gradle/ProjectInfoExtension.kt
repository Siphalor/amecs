package de.siphalor.amecs.gradle

import org.gradle.api.provider.Property

abstract class ProjectInfoExtension {
	abstract val minecraftVersionDescriptor: Property<String>
	abstract val shortVersion: Property<String>
	abstract val minecraftVersion: Property<String>
}
