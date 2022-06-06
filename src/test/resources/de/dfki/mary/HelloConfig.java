package my.world;

import marytts.config.LanguageConfig;
import marytts.exceptions.MaryConfigurationException;

public class HelloConfig extends LanguageConfig {

    public HelloConfig() throws MaryConfigurationException {
        super(HelloConfig.class.getResourceAsStream("hello.config"));
    }
}
