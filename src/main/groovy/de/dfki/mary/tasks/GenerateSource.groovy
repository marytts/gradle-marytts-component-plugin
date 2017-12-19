package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*

class GenerateSource extends DefaultTask {

    @OutputDirectory
    Provider<Directory> destDir = newOutputDirectory()

    GenerateSource() {
        destDir = project.layout.buildDirectory.dir('generatedSrc')
    }

    @TaskAction
    void generate() {
        def tree = new FileTreeBuilder(destDir.get().asFile)
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
                                   |
                                   |    Config() {
                                   |        super(Config.class.getResourceAsStream('hello.config'))
                                   |    }
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
                                   |import org.testng.annotations.Test
                                   |
                                   |class ConfigTest {
                                   |
                                   |    @Test
                                   |    public void isNotMainConfig() {
                                   |        def config = new Config()
                                   |        assert config.isMainConfig() == false
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
