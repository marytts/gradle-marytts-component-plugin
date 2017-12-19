package de.dfki.mary

import org.gradle.api.*

class MaryttsExtension {

    ComponentData component

    MaryttsExtension(Project project) {
        component = new ComponentData(project)
    }

    void component(Action<? super ComponentData> action) {
        action.execute component
    }
}
