import groovy.lang.Closure
import org.jetbrains.grammarkit.tasks.*
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.io.*
import java.nio.file.*
import java.net.URL
import java.util.stream.Collectors

val kotlinVersion: String by extra

buildscript {
	var kotlinVersion: String by extra
	var grammarKitVersion: String by extra

	grammarKitVersion = "2018.1.1"
	kotlinVersion = "1.2.31"

	repositories {
		mavenCentral()
		maven("https://jitpack.io")
	}

	dependencies {
		classpath(kotlin("gradle-plugin", kotlinVersion))
		classpath("com.github.JetBrains:gradle-grammar-kit-plugin:$grammarKitVersion")
	}
}

plugins {
	idea
	java
	id("org.jetbrains.intellij") version "0.3.1"
	kotlin("jvm") version "1.2.31"
}

allprojects {
	apply {
		plugin("org.jetbrains.grammarkit")
	}

	intellij {
		version = "2017.3"
		updateSinceUntilBuild = false
		instrumentCode = true
//		if(ext["alternative_ide_path"].toString()!="") {
//
//		}
	}
}

/*
java.sourceSets {
	"main" {
		withConvention(KotlinSourceSet::class) {
			listOf(java, kotlin).forEach {
				it.srcDirs("src/main/java", "src/main/kotlin", "gen")
			}
		}
		resources.srcDirs("src/main/resources")
	}

	"test" {
		withConvention(KotlinSourceSet::class) {
			listOf(java, kotlin).forEach {
				it.srcDirs("test")
			}
		}
		resources.srcDirs("testData")
	}
}
 */

repositories {
	mavenCentral()
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


val parserRoot = Paths.get("com", "github", "zxj5470", "weapp")!!
val lexerRoot = Paths.get("gen", "com", "github", "zxj5470", "weapp")!!
fun path(more: Iterable<*>) = more.joinToString(File.separator)
fun bnf(name: String) = Paths.get("grammar", "$name.bnf").toString()
fun flex(name: String) = Paths.get("grammar", "$name.flex").toString()


val genParser = task<GenerateParser>("genParser") {
	group = "wxapp"
	description = "Generate the Parser and PsiElement classes"
	source = bnf("wxml")
	targetRoot = "gen/"
	pathToParser = path(parserRoot + "JuliaParser.java")
	pathToPsiRoot = path(parserRoot + "psi")
	purgeOldFiles = true
}

val genLexer = task<GenerateLexer>("genLexer") {
	group = "wxapp"
	description = "Generate the Lexer"
	source = flex("wxml")
	targetDir = path(lexerRoot)
	targetClass = "WxmlLexer"
	purgeOldFiles = true
}


val cleanGenerated = task("cleanGenerated") {
	group = "wxapp"
	description = "Remove all generated codes"
	doFirst {
		delete("gen")
	}
}

java.sourceSets {
	"main"{
		withConvention(KotlinSourceSet::class) {
			listOf(java, kotlin).forEach {
				it.srcDirs("gen")
			}
		}
		resources.srcDirs("src/main/sources")
	}
}