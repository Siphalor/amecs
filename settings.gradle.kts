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

plugins {
	id("de.siphalor.minecraft-modding-toolkit.settings-plugin") version("0.1.0")
}

smcmtk {
	fabricLoomVersion = "1.15-SNAPSHOT"
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
