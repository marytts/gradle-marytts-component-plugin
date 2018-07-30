package de.dfki.mary

import de.dfki.mary.tasks.*
import org.gradle.api.*
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestReport

class ComponentPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.pluginManager.apply GroovyPlugin

        project.extensions.create 'marytts', MaryttsExtension, project

        project.repositories {
            jcenter()
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
            integrationTestCompile.extendsFrom testCompile
            integrationTestRuntime.extendsFrom testRuntime
        }

        project.dependencies {
            compile localGroovy()
            compile group: 'de.dfki.mary', name: 'marytts-runtime', version: '5.2', {
                exclude group: '*', module: 'groovy-all'
            }
            testCompile group: 'org.testng', name: 'testng', version: '6.9.13'
        }

        project.tasks.register 'generateServiceLoader', GenerateServiceLoader, {
            destFile = project.layout.buildDirectory.file('serviceLoader.txt')
        }

        project.tasks.register 'generateSource', GenerateSource, {
            destDir = project.layout.buildDirectory.dir('generatedSrc')
        }

        project.tasks.register 'generateConfig', GenerateConfig, {
            destFile = project.layout.buildDirectory.file('generated.config')
        }

        project.sourceSets {
            main {
                groovy {
                    srcDirs += "${project.generateSource.destDir.get()}/main/groovy"
                }
            }
            test {
                groovy {
                    srcDirs += "${project.generateSource.destDir.get()}/test/groovy"
                }
            }
            integrationTest {
                groovy {
                    srcDirs += "${project.generateSource.destDir.get()}/integrationTest/groovy"
                }
            }
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
            workingDir = project.buildDir
            testClassesDirs = project.sourceSets.integrationTest.output.classesDirs
            classpath = project.sourceSets.integrationTest.runtimeClasspath
            systemProperty 'log4j.logger.marytts', 'INFO,stderr'
            testLogging.showStandardStreams = true
            mustRunAfter project.tasks.named('test')
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
