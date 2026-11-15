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
				includeGroupAndSubgroups("me.hypherionmc")
			}
		}
		maven {
			url = uri("https://jitpack.io")
			mavenContent {
				includeGroupAndSubgroups("com.github")
			}
		}
		gradlePluginPortal()
		mavenLocal()
	}
}

plugins {
	id("de.siphalor.minecraft-modding-toolkit.settings-plugin") version("0.1.3")
}

smcmtk {
	fabricLoomVersion = "1.19-SNAPSHOT"
}

rootProject.name = "amecs"

includeBuild("build-logic")

include("amecs-key-modifiers")
include("amecs-key-mapping-descriptions")
include("amecs-key-mapping-matchers")
include("amecs-key-mapping-scopes")
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
