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
                                   |    public void canGetProperties() {
                                   |""".stripMargin() +
                                        project.marytts.component.config.collect { name, value ->
                                            if (value instanceof List) {
                                                return "        assert config.properties.'${name}.list' == '" + value.join(' ') + "'"
                                            } else {
                                                return "        assert config.properties.'$name' == '$value'"
                                            }
                                        }.join('\n') +
                                        """|
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
                                   |    @DataProvider
                                   |    Object[][] properties() {
                                   |        [
                                   |""".stripMargin() +
                                        project.marytts.component.config.collect { name, value ->
                                            if (value instanceof List) {
                                                return "            ['${name}.list', $value]"
                                            } else {
                                                return "            ['$name', '$value']"
                                            }
                                        }.join(',\n') +
                                        """|
                                   |        ]
                                   |    }
                                   |
                                   |    @Test(dataProvider = 'properties')
                                   |    public void canGetProperty(name, expected) {
                                   |        def actual
                                   |        switch (name) {
                                   |            case ~/.+\\.list\$/:
                                   |                actual = MaryProperties.getList(name)
                                   |                break
                                   |            default:
                                   |                actual = MaryProperties.getProperty(name)
                                   |                break
                                   |        }
                                   |        assert expected == actual
                                   |        if (expected.startsWith('jar:')) {
                                   |            assert MaryProperties.getStream(name)
                                   |        }
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
