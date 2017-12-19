package de.dfki.mary

import org.gradle.api.Action

class MaryttsExtension {
    ComponentData component = new ComponentData()

    void component(Action<? super ComponentData> action) {
        action.execute component
    }
}
