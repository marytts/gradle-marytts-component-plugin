package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*

class GenerateSource extends DefaultTask {

    @OutputFile
    final RegularFileProperty configClassFile = project.objects.fileProperty()

    @OutputFile
    final RegularFileProperty configTestFile = project.objects.fileProperty()

    @OutputFile
    final RegularFileProperty integrationTestFile = project.objects.fileProperty()

    @TaskAction
    void generate() {

        def engine = new groovy.text.GStringTemplateEngine()
        def binding = [project: project]

        def f = new InputStreamReader(getClass().getResourceAsStream('ConfigClass.groovy'))
        def template = engine.createTemplate(f).make(binding)
        configClassFile.get().asFile.text = template.toString()


        def assert_prop_str = project.marytts.component.config.collect { name, value ->
            if (value instanceof List) {
                return "        assert config.properties.'${name}.list'.tokenize().containsAll(${value.collect { '\'' + it + '\'' }})"
            } else {
                return "        assert config.properties.'$name' == '$value'"
            }
        }.join('\n')


        f = new InputStreamReader(getClass().getResourceAsStream('ConfigTest.groovy'))
        template = engine.createTemplate(f).make(binding + [assert_prop: assert_prop_str])
        configTestFile.get().asFile.text = template.toString()


        assert_prop_str = project.marytts.component.config.findAll {
            !(it.key in ['locale', 'name'])
        }.collect { name, value ->
            if (value instanceof List) {
                return "            ['${name}.list', ${value.collect { '\'' + it + '\'' }}]"
            } else {
                return "            ['$name', '$value']"
            }
        }.join(',\n')

        f = new InputStreamReader(getClass().getResourceAsStream('IntegrationTest.groovy'))
        template = engine.createTemplate(f).make(binding + [assert_prop: assert_prop_str])
        integrationTestFile.get().asFile.text = template.toString()
    }
}
