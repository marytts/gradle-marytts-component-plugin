package mypackage

import marytts.config.*
import org.testng.annotations.*

class MyComponentConfigTest {

    MyComponentConfig config

    @BeforeMethod
    void setup() {
        config = new MyComponentConfig()
    }

    @Test
    public void isNotMainConfig() {
        assert config.isMainConfig() == false
    }

    @Test
    public void testConfigBaseClass() {
        assert config instanceof MaryConfig
    }

    @Test
    public void canGetProperties() {

    }
}
