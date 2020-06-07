package $project.marytts.component.packageName

import marytts.config.*

class ${project.marytts.component.name}Config extends $project.marytts.component.configBaseClass {

    ${project.marytts.component.name}Config() {
        super(${project.marytts.component.name}Config.class.getResourceAsStream('${project.marytts.component.name.toLowerCase()}.config'))
    }
}
