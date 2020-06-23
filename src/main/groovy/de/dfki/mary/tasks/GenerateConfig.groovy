package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class GenerateConfig extends DefaultTask {

    @OutputFile
    final RegularFileProperty destFile = project.objects.fileProperty()

    @TaskAction
    void generate() {
        destFile.get().asFile.withWriter { config ->
            project.marytts.component.config.each { key, value ->
                if (value instanceof List) {
                    config.println "${key}.list = \\"
                    config.println value.collect { "    $it" }.join(' \\\n')
                } else {
                    config.println "$key = $value"
                }
            }
        }
    }
}
