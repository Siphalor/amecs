plugins {
	`java-library`
}

repositories {
	maven {
		name = "Fabric"
		url = uri("https://maven.fabricmc.net/")
		mavenContent {
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
		name = "BlameJared"
		url = uri("https://maven.blamejared.com")
		mavenContent {
			includeGroupAndSubgroups("com.blamejared")
		}
	}
	mavenLocal()
}
