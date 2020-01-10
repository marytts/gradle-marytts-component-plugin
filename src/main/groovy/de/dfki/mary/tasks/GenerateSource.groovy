package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.*

class GenerateSource extends DefaultTask {

    @OutputDirectory
    final DirectoryProperty destDir = project.objects.directoryProperty()

    @TaskAction
    void generate() {
        project.delete destDir.asFileTree
        def tree = new FileTreeBuilder(destDir.get().asFile)
        tree {
            main {
                groovy {
                    "$project.marytts.component.packagePath" {
                        "${project.marytts.component.name}Config.groovy"(
                                """|package $project.marytts.component.packageName
                                   |
                                   |import marytts.config.*
                                   |
                                   |class ${
                                    project.marytts.component.name
                                }Config extends $project.marytts.component.configBaseClass {
                                   |
                                   |    ${project.marytts.component.name}Config() {
                                   |        super(${project.marytts.component.name}Config.class.getResourceAsStream('${
                                    project.marytts.component.name.toLowerCase()
                                }.config'))
                                   |    }
                                   |}
                                   |""".stripMargin()
                        )
                    }
                }
            }
            test {
                groovy {
                    "$project.marytts.component.packagePath" {
                        "${project.marytts.component.name}ConfigTest.groovy"(
                                """|package $project.marytts.component.packageName
                                   |
                                   |import marytts.config.*
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
                                   |    public void testConfigBaseClass() {
                                   |        assert config instanceof $project.marytts.component.configBaseClass
                                   |    }
                                   |
                                   |    @Test
                                   |    public void canGetProperties() {
                                   |""".stripMargin() +
                                        project.marytts.component.config.collect { name, value ->
                                            if (value instanceof List) {
                                                return "        assert config.properties.'${name}.list'.tokenize().containsAll(${value.collect { '\'' + it + '\'' }})"
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
                    "$project.marytts.component.packagePath" {
                        "Load${project.marytts.component.name}IT.groovy"(
                                """|package $project.marytts.component.packageName
                                   |
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
                                        project.marytts.component.config.findAll {
                                            !(it.key in ['locale', 'name'])
                                        }.collect { name, value ->
                                            if (value instanceof List) {
                                                return "            ['${name}.list', ${value.collect { '\'' + it + '\'' }}]"
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
                                   |                assert actual.containsAll(expected)
                                   |                break
                                   |            default:
                                   |                actual = MaryProperties.getProperty(name)
                                   |                assert expected == actual
                                   |                break
                                   |        }
                                   |        if ("\$expected".startsWith('jar:')) {
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
