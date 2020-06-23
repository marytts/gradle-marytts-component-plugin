package de.dfki.mary

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property

class MaryttsExtension {

    ComponentData component

    Property<String> version

    MaryttsExtension(Project project) {
        component = new ComponentData(project)
        this.version = project.objects.property(String)
    }

    void component(Action<? super ComponentData> action) {
        action.execute component
    }

    String getVersion() {
        this.version.getOrElse()
    }

    void setVersion(String version) {
        this.version.set(version)
    }
}
