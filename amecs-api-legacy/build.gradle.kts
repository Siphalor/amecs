import de.siphalor.amecs.gradle.ProjectInfoExtension

plugins {
	id("de.siphalor.amecs.core")
	id("de.siphalor.amecs.publishing.maven")
}

dependencies {
	implementation(project(":amecs-key-mapping-descriptions", configuration = "namedElements"))
	include(project(":amecs-key-mapping-descriptions"))
	implementation(project(":amecs-key-modifiers", configuration = "namedElements"))
	include(project(":amecs-key-modifiers"))
	implementation(project(":amecs-mouse-inputs", configuration = "namedElements"))
	include(project(":amecs-mouse-inputs"))
	implementation(project(":amecs-priority-key-mappings", configuration = "namedElements"))
	include(project(":amecs-priority-key-mappings"))
}
