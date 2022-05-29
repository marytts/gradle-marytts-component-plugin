package de.dfki.mary

import de.dfki.mary.tasks.*
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestReport

class ComponentPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.pluginManager.apply JavaLibraryPlugin
        project.pluginManager.apply GroovyPlugin

        project.sourceCompatibility = JavaVersion.VERSION_1_8

        project.extensions.create 'marytts', MaryttsExtension, project
        project.marytts {
            version = "5.2.1"
        }

        project.repositories {
            mavenCentral()
            exclusiveContent {
                forRepository {
                    maven {
                        name 'DFKI-MLT'
                        url 'https://mlt.jfrog.io/artifactory/mlt-mvn-releases-local'
                    }
                }
                filter {
                    includeGroup 'de.dfki.lt.jtok'
                }
            }
        }

        project.sourceSets {
            integrationTest {
                java {
                    compileClasspath += main.output + test.output
                    runtimeClasspath += main.output + test.output
                }
            }
        }

        project.configurations {
            integrationTestImplementation.extendsFrom testImplementation
            integrationTestRuntimeOnly.extendsFrom testRuntimeOnly
        }

        project.dependencies {
            api group: 'de.dfki.mary', name: 'marytts-runtime', version: project.marytts.version, {
                exclude group: '*', module: 'groovy-all'
                exclude group: 'com.twmacinta', module: 'fast-md5'
                exclude group: 'gov.nist.math', module: 'Jampack'
            }
            testImplementation localGroovy()
            testImplementation group: 'org.testng', name: 'testng', version: '7.5'
        }

        project.tasks.register 'generateServiceLoader', GenerateServiceLoader, {
            destFile = project.layout.buildDirectory.file('serviceLoader.txt')
        }

        project.tasks.register('unpackSourceTemplates', UnpackSourceTemplates) {
            resourceNames = ['ConfigClass.java']
            destDir = project.layout.buildDirectory.dir('unpackedSrcTemplates')
        }

        project.tasks.register('unpackTestSourceTemplates', UnpackSourceTemplates) {
            resourceNames = ['ConfigTest.groovy']
            destDir = project.layout.buildDirectory.dir('unpackedTestSrcTemplates')
        }

        project.tasks.register('unpackIntegrationTestSourceTemplates', UnpackSourceTemplates) {
            resourceNames = ['IntegrationTest.groovy']
            destDir = project.layout.buildDirectory.dir('unpackedIntegrationTestSrcTemplates')
        }

        def generateSourceTask = project.tasks.register 'generateSource', GenerateSource, {
            destDir = project.layout.buildDirectory.dir('generatedSrc')
        }

        def generateTestSourceTask = project.tasks.register 'generateTestSource', GenerateTestSource, {
            destDir = project.layout.buildDirectory.dir('generatedTestSrc')
        }

        def generateIntegrationTestSourceTask = project.tasks.register 'generateIntegrationTestSource', GenerateIntegrationTestSource, {
            destDir = project.layout.buildDirectory.dir('generatedIntegrationTestSrc')
        }

        project.tasks.register 'generateConfig', GenerateConfig, {
            destFile = project.layout.buildDirectory.file('generated.config')
        }

        project.sourceSets {
            main.java.srcDir generateSourceTask
            test.groovy.srcDir generateTestSourceTask
            integrationTest.groovy.srcDir generateIntegrationTestSourceTask
        }

        project.processResources {
            from project.tasks.named('generateServiceLoader'), {
                rename { 'META-INF/services/marytts.config.MaryConfig' }
            }
            from project.tasks.named('generateConfig'), {
                rename {
                    "$project.marytts.component.packagePath/${project.marytts.component.name.toLowerCase()}.config"
                }
            }
        }

        project.compileGroovy {
            dependsOn project.tasks.named('generateSource')
        }

        project.tasks.register 'integrationTest', Test, {
            group 'Verification'
            description 'Runs the integration tests.'
            workingDir = project.buildDir
            testClassesDirs = project.sourceSets.integrationTest.output.classesDirs
            classpath = project.sourceSets.integrationTest.runtimeClasspath
            systemProperty 'log4j.logger.marytts', 'INFO,stderr'
            testLogging.showStandardStreams = true
            shouldRunAfter project.tasks.named('test')
        }

        project.tasks.withType(Test) {
            useTestNG()
            testLogging {
                exceptionFormat = 'full'
            }
        }

        project.tasks.register 'testReports', TestReport, {
            reportOn project.tasks.withType(Test)
            destinationDir = project.file("$project.testReportDir/all")
        }

        project.tasks.named('check').configure {
            dependsOn project.tasks.named('testReports')
        }
    }
}
