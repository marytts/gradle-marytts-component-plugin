package de.dfki.mary

import de.dfki.mary.tasks.*
import org.gradle.api.*
import org.gradle.api.plugins.GroovyPlugin

class ComponentPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.pluginManager.apply GroovyPlugin

        project.extensions.create('marytts', MaryttsExtension, project)

        project.repositories {
            jcenter()
        }

        project.dependencies {
            compile localGroovy()
            compile group: 'de.dfki.mary', name: 'marytts-runtime', version: '5.2', {
                exclude module: 'groovy-all'
            }
            testCompile group: 'org.testng', name: 'testng', version: '6.9.13'
        }

        project.tasks.create('generateSource', GenerateSource) {
            destDir = project.layout.buildDirectory.dir('generatedSrc')
        }

        project.tasks.create('generateConfig', GenerateConfig) {
            destFile = project.layout.buildDirectory.file("hello.config")
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
        }

        project.processResources {
            from project.generateConfig, {
                rename { "$project.marytts.component.packageName/$it" }
            }
        }

        project.test {
            useTestNG()
            testLogging {
                exceptionFormat = 'full'
            }
        }
    }
}
