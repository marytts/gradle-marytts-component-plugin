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
                                   |import marytts.config.MaryConfig
                                   |
                                   |class Config extends MaryConfig {
                                   |}
                                   |""".stripMargin()
                        )
                    }
                }
            }
            test {
                groovy {
                    marytts {
                        'ConfigTest.groovy'(
                                """|package marytts
                                   |
                                   |class ConfigTest {
                                   |    @Test
                                   |    public void isNotMainConfig() {
                                   |        def config = new Config()
                                   |        assert m.isMainConfig() == false
                                   |    }
                                   |}
                                   |""".stripMargin()
                        )
                    }
                }
            }
        }
    }
}
