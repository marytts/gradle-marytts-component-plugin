package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class GenerateIntegrationTestSource extends DefaultTask {
    @OutputDirectory
    final DirectoryProperty integrationTestDirectory = project.objects.directoryProperty()

    @TaskAction
    void generate() {

        def engine = new groovy.text.GStringTemplateEngine()
        def binding = [project: project]

        def assert_prop_str = project.marytts.component.config.findAll {
            !(it.key in ['locale', 'name'])
        }.collect { name, value ->
            if (value instanceof List) {
                return "            ['${name}.list', ${value.collect { '\'' + it + '\'' }}]"
            } else {
                return "            ['$name', '$value']"
            }
        }.join(',\n')

        def templateStream = new InputStreamReader(getClass().getResourceAsStream('IntegrationTest.groovy'))
        def template = engine.createTemplate(templateStream).make(binding + [assert_prop: assert_prop_str])
        def integrationTestFile = new File(integrationTestDirectory.get().asFile, "${project.marytts.component.packagePath}/Load${project.marytts.component.name}IT.groovy")
        integrationTestFile.parentFile.mkdirs()
        integrationTestFile.text = template.toString()
    }
}
