package de.dfki.mary.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class UnpackSourceTemplates extends DefaultTask {
    @Input
    final ListProperty<String> resourceNames = project.objects.listProperty(String)

    @OutputDirectory
    final DirectoryProperty destDir = project.objects.directoryProperty()

    @TaskAction
    void unpack() {
        resourceNames.get().each { resourceName ->
            destDir.file(resourceName).get().asFile.withWriter {
                def resource = this.class.getResourceAsStream(resourceName)
                assert resource: "Source template $resourceName could not be found for unpacking"
                it << resource
            }
        }
    }
}
