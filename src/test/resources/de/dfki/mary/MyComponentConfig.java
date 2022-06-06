package mypackage;

import marytts.config.MaryConfig;
import marytts.exceptions.MaryConfigurationException;

public class MyComponentConfig extends MaryConfig {

    public MyComponentConfig() throws MaryConfigurationException {
        super(MyComponentConfig.class.getResourceAsStream("mycomponent.config"));
    }
}
