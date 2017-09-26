package de.dfki.mary

import de.dfki.mary.tasks.GenerateSource
import org.gradle.api.*
import org.gradle.api.plugins.GroovyPlugin

class ComponentPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.pluginManager.apply GroovyPlugin

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

        project.tasks.maybeCreate('generateSource', GenerateSource)

        project.sourceSets {
            main {
                groovy {
                    srcDirs += "$project.generateSource.destDir/main/groovy"
                }
            }
            test {
                groovy {
                    srcDirs += "$project.generateSource.destDir/test/groovy"
                }
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
