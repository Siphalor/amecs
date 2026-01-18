import java.util.*

pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		maven {
			name = "Siphalor's Maven"
			url = uri("https://maven.siphalor.de")
		}
		gradlePluginPortal()
		mavenLocal()
	}
}


val properties = Properties()
properties.load(file("gradle.properties").inputStream())

dependencyResolutionManagement {
	versionCatalogs {
		create("mcLibs") {
			from(files("gradle/mc-${properties["minecraft.version.descriptor"]}/mc.versions.toml"))
		}
	}
}

rootProject.name = "amecs-core"

include("amecs-key-modifiers")
include("amecs-priority-key-mappings")
include("amecs-mouse-inputs")

include("amecs-api-legacy")

include("testmod")

fun includeAs(name: String, path: String) {
	include(name)
	project(":$name").projectDir = file(path)
}
