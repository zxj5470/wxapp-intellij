import groovy.lang.Closure
import org.gradle.api.internal.HasConvention
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import java.io.*
import java.nio.file.*
import java.net.URL
import java.util.stream.Collectors

val kotlinVersion: String by extra
val alternative_ide_path: String by extra

buildscript {
	var kotlinVersion: String by extra

	kotlinVersion = "1.2.31"

	repositories {
		mavenCentral()
		maven("https://jitpack.io")
	}

	dependencies {
		classpath(kotlin("gradle-plugin", kotlinVersion))
	}
}

plugins {
	idea
	java
	id("org.jetbrains.intellij") version "0.3.1"
	kotlin("jvm") version "1.2.31"
}

intellij {
	version = "2018.1"
	type = "IU"
	updateSinceUntilBuild = false
	instrumentCode = true
	// use WebStorm
	alternativeIdePath = alternative_ide_path
	setPlugins("JavaScriptLanguage", "CSS")
}


val SourceSet.kotlin
	get() = (this as HasConvention)
			.convention
			.getPlugin(KotlinSourceSet::class.java)
			.kotlin

//tasks.withType<PatchPluginXmlTask> {
//			changeNotes(file("change-notes.html").readText())
//	pluginDescription(file("description.html").readText())
//		version(pluginComingVersion)
//		pluginId(packageName)
//}

repositories {
	mavenCentral()
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(kotlin("stdlib", kotlinVersion))
	compile(kotlin("stdlib-jdk8", kotlinVersion).toString()) {
		exclude(module = "kotlin-runtime")
		exclude(module = "kotlin-reflect")
		exclude(module = "kotlin-stdlib")
	}
	testCompile(kotlin("test-junit", kotlinVersion))
	testCompile("junit", "junit", "4.12")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

java.sourceSets {
	"main"{
		withConvention(KotlinSourceSet::class) {
			listOf(java, kotlin).forEach {
				it.srcDirs("src/main/fake")
			}
		}
		resources.srcDirs("src/main/sources")
	}
	"test"{
		resources.srcDirs("testData")
	}
}