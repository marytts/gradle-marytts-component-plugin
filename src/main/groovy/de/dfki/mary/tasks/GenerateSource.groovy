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
                    "$project.marytts.component.packageName" {
                        "${project.marytts.component.name}Config.groovy"(
                                """|package $project.marytts.component.packageName
                                   |
                                   |import marytts.config.MaryConfig
                                   |
                                   |class ${project.marytts.component.name}Config extends MaryConfig {
                                   |
                                   |    ${project.marytts.component.name}Config() {
                                   |        super(${
                                    project.marytts.component.name
                                }Config.class.getResourceAsStream('hello.config'))
                                   |    }
                                   |}
                                   |""".stripMargin()
                        )
                    }
                }
            }
            test {
                groovy {
                    "$project.marytts.component.packageName" {
                        "${project.marytts.component.name}ConfigTest.groovy"(
                                """|package $project.marytts.component.packageName
                                   |
                                   |import org.testng.annotations.Test
                                   |
                                   |class ${project.marytts.component.name}ConfigTest {
                                   |
                                   |    @Test
                                   |    public void isNotMainConfig() {
                                   |        def config = new ${project.marytts.component.name}Config()
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
