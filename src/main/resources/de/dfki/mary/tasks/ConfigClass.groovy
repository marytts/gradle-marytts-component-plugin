package $project.marytts.component.packageName

import marytts.config.$project.marytts.component.configBaseClass
import marytts.exceptions.MaryConfigurationException;

class ${project.marytts.component.name}Config extends $project.marytts.component.configBaseClass {

    ${project.marytts.component.name}Config() throws MaryConfigurationException {
        super(${project.marytts.component.name}Config.class.getResourceAsStream('${project.marytts.component.name.toLowerCase()}.config'))
    }
}
