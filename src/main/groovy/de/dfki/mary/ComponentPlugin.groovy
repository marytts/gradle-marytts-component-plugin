package de.dfki.mary

import org.gradle.api.*
import org.gradle.api.plugins.GroovyPlugin

class ComponentPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.pluginManager.apply GroovyPlugin
    }
}
