package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*

class GenerateConfig extends DefaultTask {

    @OutputFile
    final RegularFileProperty destFile = newOutputFile()

    @TaskAction
    void generate() {
        destFile.get().asFile.withWriter { config ->
            project.marytts.component.config.each { key, value ->
                config.println "$key = $value"
            }
        }
    }
}
