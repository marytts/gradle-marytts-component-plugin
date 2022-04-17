package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class GenerateSource extends DefaultTask {
    @OutputDirectory
    final DirectoryProperty srcDirectory = project.objects.directoryProperty()

    @TaskAction
    void generate() {

        def engine = new groovy.text.GStringTemplateEngine()
        def binding = [project: project]

        def templateStream = new InputStreamReader(getClass().getResourceAsStream('ConfigClass.java'))
        def template = engine.createTemplate(templateStream).make(binding)
        def configClassFile = new File(srcDirectory.get().asFile, "${project.marytts.component.packagePath}/${project.marytts.component.name}Config.java")
        configClassFile.parentFile.mkdirs()
        configClassFile.text = template.toString()
    }
}
