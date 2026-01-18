import dev.panuszewski.gradle.pluginMarker

plugins {
	`kotlin-dsl`
}

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
	mavenCentral()
}

dependencies {
	implementation(pluginMarker(libs.plugins.changelog))
	implementation(pluginMarker(libs.plugins.loom))
	implementation(pluginMarker(libs.plugins.licenser))
	implementation(pluginMarker(libs.plugins.jcyo))
	implementation(pluginMarker(libs.plugins.modPublisher))
}
