package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*

class GenerateConfig extends DefaultTask {

    @OutputFile
    RegularFileProperty destFile = newOutputFile()

    GenerateConfig() {
        destFile.set(project.layout.buildDirectory.file("hello.config"))
    }

    @TaskAction
    void generate() {
        destFile.get().asFile.withWriter { config ->
            config.println "hello = World"
        }
    }
}
