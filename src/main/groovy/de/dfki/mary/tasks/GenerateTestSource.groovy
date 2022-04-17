package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class GenerateTestSource extends DefaultTask {
    @OutputDirectory
    final DirectoryProperty testDirectory = project.objects.directoryProperty()

    @TaskAction
    void generate() {

        def engine = new groovy.text.GStringTemplateEngine()
        def binding = [project: project]

        def assert_prop_str = project.marytts.component.config.collect { name, value ->
            if (value instanceof List) {
                return "        assert config.properties.'${name}.list'.tokenize().containsAll(${value.collect { '\'' + it + '\'' }})"
            } else {
                return "        assert config.properties.'$name' == '$value'"
            }
        }.join('\n')


        def templateStream = new InputStreamReader(getClass().getResourceAsStream('ConfigTest.groovy'))
        def template = engine.createTemplate(templateStream).make(binding + [assert_prop: assert_prop_str])
        def configTestFile = new File(testDirectory.get().asFile, "${project.marytts.component.packagePath}/${project.marytts.component.name}ConfigTest.groovy")
        configTestFile.parentFile.mkdirs()
        configTestFile.text = template.toString()
    }
}
