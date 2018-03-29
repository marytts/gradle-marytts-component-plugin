package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.*

class GenerateSource extends DefaultTask {

    @OutputDirectory
    final DirectoryProperty destDir = newOutputDirectory()

    @TaskAction
    void generate() {
        project.delete destDir.asFileTree
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
                                   |        super(${project.marytts.component.name}Config.class.getResourceAsStream('hello.config'))
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
                                   |import org.testng.annotations.*
                                   |
                                   |class ${project.marytts.component.name}ConfigTest {
                                   |
                                   |    ${project.marytts.component.name}Config config
                                   |
                                   |    @BeforeMethod
                                   |    void setup() {
                                   |        config = new ${project.marytts.component.name}Config()
                                   |    }
                                   |
                                   |    @Test
                                   |    public void isNotMainConfig() {
                                   |        assert config.isMainConfig() == false
                                   |    }
                                   |
                                   |    @Test
                                   |    public void canGetProperty() {
                                   |        assert config.properties.hello == 'World'
                                   |    }
                                   |}
                                   |""".stripMargin()
                        )
                    }
                }
            }
            integrationTest {
                groovy {
                    "$project.marytts.component.packageName" {
                        "Load${project.marytts.component.name}IT.groovy"(
                                """|package $project.marytts.component.packageName
                                   |
                                   |import marytts.config.MaryConfig
                                   |import marytts.server.MaryProperties
                                   |import marytts.util.MaryRuntimeUtils
                                   |
                                   |import org.testng.annotations.*
                                   |
                                   |class Load${project.marytts.component.name}IT {
                                   |
                                   |    @BeforeMethod
                                   |    void setup() {
                                   |        MaryRuntimeUtils.ensureMaryStarted()
                                   |    }
                                   |
                                   |    @Test
                                   |    public void canGetProperty() {
                                   |        def expected = 'World'
                                   |        def actual = MaryProperties.getProperty('hello')
                                   |        assert expected == actual
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
