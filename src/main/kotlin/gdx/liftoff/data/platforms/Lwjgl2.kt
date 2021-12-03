package gdx.liftoff.data.platforms

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.GdxPlatform

/**
 * Represents the legacy desktop backend, which has been replaced in practice by LWJGL3.
 */
@GdxPlatform
class Lwjgl2 : Platform {
    companion object {
        const val ID = "lwjgl2"
        const val ORDER = Headless.ORDER + 1
    }

    override val id = ID
    override val order = ORDER
    override val isStandard = false // use lwjgl3 instead
    override fun createGradleFile(project: Project): GradleFile = Lwjgl2GradleFile(project)

    override fun initiate(project: Project) {
        // Adding game icons:
        arrayOf(16, 32, 64, 128)
            .map { "libgdx$it.png" }
            .forEach { icon ->
                project.files.add(
                    CopiedFile(
                        projectName = ID,
                        path = path("src", "main", "resources", icon),
                        original = path("icons", icon)
                    )
                )
            }

        addGradleTaskDescription(project, "run", "starts the application.")
        addGradleTaskDescription(project, "jar", "builds application's runnable jar, which can be found at `$id/build/libs`.")
    }
}

/**
 * Gradle file of the legacy desktop project.
 */
class Lwjgl2GradleFile(val project: Project) : GradleFile(Lwjgl2.ID) {
    init {
        dependencies.add("project(':${Core.ID}')")
        addDependency("com.badlogicgames.gdx:gdx-backend-lwjgl:\$gdxVersion")
        addDependency("com.badlogicgames.gdx:gdx-platform:\$gdxVersion:natives-desktop")
    }

    override fun getContent(): String = """apply plugin: 'application'

sourceSets.main.resources.srcDirs += [ rootProject.file('assets').path ]
mainClassName = '${project.basic.rootPackage}.desktop.DesktopLauncher'
eclipse.project.name = appName + '-desktop'
sourceCompatibility = ${project.advanced.desktopJavaVersion}

dependencies {
${joinDependencies(dependencies)}}

jar {
	archiveBaseName.set(appName)
// the duplicatesStrategy matters starting in Gradle 7.0; this setting works.
	duplicatesStrategy(DuplicatesStrategy.EXCLUDE)
	dependsOn configurations.runtimeClasspath
	from { configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) } }
// these "exclude" lines remove some unnecessary duplicate files in the output JAR.
	exclude('META-INF/INDEX.LIST', 'META-INF/*.SF', 'META-INF/*.DSA', 'META-INF/*.RSA')
	dependencies {
		exclude('META-INF/INDEX.LIST', 'META-INF/maven/**')
	}
// setting the manifest makes the JAR runnable.
	manifest {
		attributes 'Main-Class': project.mainClassName
	}
// this last step may help on some OSes that need extra instruction to make runnable JARs.
	doLast {
		file(archiveFile).setExecutable(true, false)
	}
}

// Equivalent to the jar task; here for compatibility with gdx-setup.
task dist(dependsOn: [jar]) {
}

run {
	ignoreExitValue = true
}
"""
}