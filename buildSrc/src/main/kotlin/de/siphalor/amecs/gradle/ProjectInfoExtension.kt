package de.siphalor.amecs.gradle

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class ProjectInfoExtension {
	abstract val modId: Property<String>
	abstract val shortVersion: Property<String>

	abstract val minecraftVersionDescriptor: Property<String>
	abstract val minecraftVersion: Property<String>
	abstract val minecraftVersionTitle: Property<String>
	abstract val supportedMinecraftVersions: ListProperty<String>
}
