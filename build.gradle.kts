import org.gradle.api.internal.HasConvention
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val alternative_ide_path: String by extra

val commitHash = kotlin.run {
	val process: Process = Runtime.getRuntime().exec("git rev-parse --short HEAD")
	process.waitFor()
	@Suppress("RemoveExplicitTypeArguments")
	val output = process.inputStream.use {
		process.inputStream.use {
			it.readBytes().let<ByteArray, String>(::String)
		}
	}
	process.destroy()
	output.trim()
}
val isCI = !System.getenv("CI").isNullOrBlank()
val pluginComingVersion = "0.2.5"
val pluginVersion = if (isCI) "$pluginComingVersion-$commitHash" else pluginComingVersion
val packageName = "com.github.zxj5470.wxapp"

group = packageName
version = pluginVersion

plugins {
	idea
	java
	id("org.jetbrains.intellij") version "0.4.8"
	kotlin("jvm") version "1.3.30"
}
allprojects {
	intellij {
		version = "2018.3"
		type = "IU"
		updateSinceUntilBuild = false
		instrumentCode = true
		// use WebStorm
		alternativeIdePath = alternative_ide_path
		setPlugins("JavaScriptLanguage", "CSS")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "1.8"
		freeCompilerArgs = listOf("-Xjvm-default=enable")
	}
}

val SourceSet.kotlin
	get() = (this as HasConvention)
			.convention
			.getPlugin(KotlinSourceSet::class.java)
			.kotlin

tasks.withType<PatchPluginXmlTask> {
	pluginDescription(file("description.html").readText())
	changeNotes(file("change-notes.html").readText())
	version(pluginComingVersion)
}


dependencies {
	compileOnly(kotlin("stdlib-jdk8"))
	testCompile(kotlin("test-junit"))
	testCompile("junit", "junit", "4.12")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

repositories { mavenCentral() }