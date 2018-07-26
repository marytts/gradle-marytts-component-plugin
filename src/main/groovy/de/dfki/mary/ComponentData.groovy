package de.dfki.mary

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.yaml.snakeyaml.Yaml

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

    String getPackagePath() {
        getPackageName().replaceAll('\\.', '/')
    }

    Map getConfig() {
        this.config.getOrElse([:])
    }

    void setConfig(Map config) {
        this.config.set(config)
    }

    void config(args) {
        switch (args.getClass()) {
            case Map:
                def config = new ConfigObject()
                def configFile
                try {
                    configFile = project.file(args.from)
                } catch (ex) {
                    throw new InvalidUserDataException("Must supply a 'from:' argument with a readable YAML file", ex)
                }
                config << new Yaml().load(configFile.newReader('UTF-8'))
                setConfig config.flatten()
                break
            default:
                throw new InvalidUserDataException("Could not load component configurations from marytts.component.config")
                break
        }
    }
}
