import dev.panuszewski.gradle.pluginMarker

plugins {
	`kotlin-dsl`
}

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
	mavenCentral()
}

dependencies {
	implementation(pluginMarker(libs.plugins.loom))
	implementation(pluginMarker(libs.plugins.licenser))
	implementation(pluginMarker(libs.plugins.jcyo))
}
