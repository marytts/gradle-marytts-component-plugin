package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class GenerateSource extends DefaultTask {
    @OutputDirectory
    final DirectoryProperty srcDirectory = project.objects.directoryProperty()

    @OutputDirectory
    final DirectoryProperty testDirectory = project.objects.directoryProperty()

    @OutputDirectory
    final DirectoryProperty integrationTestDirectory = project.objects.directoryProperty()

    @TaskAction
    void generate() {

        def engine = new groovy.text.GStringTemplateEngine()
        def binding = [project: project]

        def templateStream = new InputStreamReader(getClass().getResourceAsStream('ConfigClass.groovy'))
        def template = engine.createTemplate(templateStream).make(binding)
        def configClassFile = new File(srcDirectory.get().asFile, "${project.marytts.component.packagePath}/${project.marytts.component.name}Config.groovy")
        configClassFile.parentFile.mkdirs()
        configClassFile.text = template.toString()

        def assert_prop_str = project.marytts.component.config.collect { name, value ->
            if (value instanceof List) {
                return "        assert config.properties.'${name}.list'.tokenize().containsAll(${value.collect { '\'' + it + '\'' }})"
            } else {
                return "        assert config.properties.'$name' == '$value'"
            }
        }.join('\n')


        templateStream = new InputStreamReader(getClass().getResourceAsStream('ConfigTest.groovy'))
        template = engine.createTemplate(templateStream).make(binding + [assert_prop: assert_prop_str])
        def configTestFile = new File(testDirectory.get().asFile, "${project.marytts.component.packagePath}/${project.marytts.component.name}ConfigTest.groovy")
        configTestFile.parentFile.mkdirs()
        configTestFile.text = template.toString()


        assert_prop_str = project.marytts.component.config.findAll {
            !(it.key in ['locale', 'name'])
        }.collect { name, value ->
            if (value instanceof List) {
                return "            ['${name}.list', ${value.collect { '\'' + it + '\'' }}]"
            } else {
                return "            ['$name', '$value']"
            }
        }.join(',\n')

        templateStream = new InputStreamReader(getClass().getResourceAsStream('IntegrationTest.groovy'))
        template = engine.createTemplate(templateStream).make(binding + [assert_prop: assert_prop_str])
        def integrationTestFile = new File(integrationTestDirectory.get().asFile, "${project.marytts.component.packagePath}/Load${project.marytts.component.name}IT.groovy")
        integrationTestFile.parentFile.mkdirs()
        integrationTestFile.text = template.toString()
    }
}
