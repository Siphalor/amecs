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

rootProject.name = "nmuk"
