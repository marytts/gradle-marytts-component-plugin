package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*

class GenerateServiceLoader extends DefaultTask {

    @OutputFile
    final RegularFileProperty destFile = newOutputFile()

    @TaskAction
    void generate() {
        destFile.get().asFile.text = "${project.marytts.component.packageName}.${project.marytts.component.name}Config"
    }
}
