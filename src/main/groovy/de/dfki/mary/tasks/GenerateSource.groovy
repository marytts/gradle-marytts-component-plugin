package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

class GenerateSource extends DefaultTask {

    @OutputDirectory
    File destDir = project.file("$project.buildDir/generatedSource")

    @TaskAction
    void generate() {
        def tree = new FileTreeBuilder(destDir)
        tree {
            main {
                groovy {
                    marytts {
                        'Config.groovy'(
                                """|package marytts
                                   |
                                   |class Config extends MaryConfig {
                                   |}
                                   |""".stripMargin()
                        )
                    }
                }
            }
        }
    }
}
