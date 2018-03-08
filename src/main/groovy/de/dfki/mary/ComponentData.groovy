package de.dfki.mary

import org.gradle.api.Project
import org.gradle.api.provider.Property

class ComponentData {

    Project project

    Property<String> name

    Property<String> packageName

    ComponentData(Project project) {
        this.project = project
        name = project.objects.property(String)
        packageName = project.objects.property(String)
    }

    void setName(String name) {
        this.name.set(name)
    }

    void setPackageName(String packageName) {
        this.packageName.set(packageName)
    }

    String getName() {
        name.getOrElse('MyComponent')
    }

    String getPackageName() {
        packageName.getOrElse('mypackage')
    }
}
