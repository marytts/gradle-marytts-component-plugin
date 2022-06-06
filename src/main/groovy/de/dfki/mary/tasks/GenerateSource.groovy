package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class GenerateSource extends DefaultTask {

    @OutputDirectory
    final DirectoryProperty destDir = project.objects.directoryProperty()

    @TaskAction
    void generate() {
        project.copy {
            into destDir
            from project.tasks.findByName('unpackSourceTemplates')
            eachFile { file ->
                if (file.name == 'ConfigClass.java')
                    file.name = "${project.marytts.component.name}Config.java"
                file.path = "$project.marytts.component.packagePath/$file.name"
            }
            expand project.properties
        }
    }
}
