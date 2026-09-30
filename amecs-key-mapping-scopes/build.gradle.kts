plugins {
	id("de.siphalor.amecs.core")
	id("de.siphalor.amecs.publishing.maven")
}

dependencies {
	implementation(project(":amecs-key-mapping-matchers", configuration = "namedElements"))
}
