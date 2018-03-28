package de.dfki.mary

import org.gradle.api.Project
import org.gradle.api.provider.Property

class ComponentData {

    Project project

    Property<String> name

    Property<String> packageName

    Property<Map> config

    ComponentData(Project project) {
        this.project = project
        name = project.objects.property(String)
        packageName = project.objects.property(String)
        config = project.objects.property(Map)
    }

    String getName() {
        this.name.getOrElse('MyComponent')
    }

    void setName(String name) {
        this.name.set(name)
    }

    String getPackageName() {
        this.packageName.getOrElse('mypackage')
    }

    void setPackageName(String packageName) {
        this.packageName.set(packageName)
    }

    Map getConfig() {
        this.config.getOrElse([:])
    }

    void setConfig(Map config) {
        this.config.set(config)
    }
}
