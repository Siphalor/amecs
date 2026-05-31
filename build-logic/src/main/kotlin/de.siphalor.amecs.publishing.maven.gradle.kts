import de.siphalor.amecs.gradle.ProjectInfoExtension

plugins {
	`java-library`
	`maven-publish`
	id("de.siphalor.amecs.project-info")
}

val projectInfo = extensions.getByType<ProjectInfoExtension>()

publishing {
	publications {
		create<MavenPublication>("mod") {
			afterEvaluate {
				if ("${project.group}".endsWith(".${project.name}")) {
					groupId = "${project.group}"
				} else {
					groupId = "${project.group}.${project.name}"
				}
			}
			artifactId = "${project.name}-mc${projectInfo.minecraftVersionDescriptor.get()}"
			version = projectInfo.shortVersion.get()

			from(components["java"])

			pom {
				url = "https://github.com/Siphalor/amecs"
			}
		}
	}

	repositories {
		if (project.hasProperty("siphalor.maven.user")) {
			maven {
				name = "Siphalor"
				url = uri("https://maven.siphalor.de/upload.php")
				credentials {
					username = project.property("siphalor.maven.user") as String
					password = project.property("siphalor.maven.password") as String
				}
			}
		}
	}
}
