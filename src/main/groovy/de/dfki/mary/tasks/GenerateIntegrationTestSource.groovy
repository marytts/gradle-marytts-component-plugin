package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class GenerateIntegrationTestSource extends DefaultTask {

    @OutputDirectory
    final DirectoryProperty destDir = project.objects.directoryProperty()

    @TaskAction
    void generate() {
        project.copy {
            into destDir
            from project.tasks.findByName('unpackIntegrationTestSourceTemplates')
            eachFile { file ->
                if (file.name == 'IntegrationTest.groovy')
                    file.name = "Load${project.marytts.component.name}IT.groovy"
                file.path = "$project.marytts.component.packagePath/$file.name"
            }
            expand project.properties
        }
    }
}
