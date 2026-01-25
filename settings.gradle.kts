import java.util.*

pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
			mavenContent {
				includeGroupAndSubgroups("fabric-loom")
				includeGroupAndSubgroups("net.fabricmc")
			}
		}
		maven {
			name = "Siphalor's Maven"
			url = uri("https://maven.siphalor.de")
			mavenContent {
				includeGroupAndSubgroups("de.siphalor")
			}
		}
		maven {
			url = uri("https://maven.firstdark.dev/releases")
			mavenContent {
				includeGroupAndSubgroups("com.hypherionmc")
			}
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

rootProject.name = "amecs"

include("amecs-key-modifiers")
include("amecs-key-mapping-descriptions")
include("amecs-mouse-inputs")
include("amecs-priority-key-mappings")

include("amecs-api-legacy")
include("amecs-api-legacy-dummy")

include("amecs-bundle")
include("nmuk")

include("testmod")

fun includeAs(name: String, path: String) {
	include(name)
	project(":$name").projectDir = file(path)
}
