package $project.marytts.component.packageName

import marytts.config.*
import org.testng.annotations.*

class ${project.marytts.component.name}ConfigTest {

    ${project.marytts.component.name}Config config

    @BeforeMethod
    void setup() {
        config = new ${project.marytts.component.name}Config()
    }

    @Test
    public void isNotMainConfig() {
        assert config.isMainConfig() == false
    }

    @Test
    public void testConfigBaseClass() {
        assert config instanceof $project.marytts.component.configBaseClass
    }

    @Test
    public void canGetProperties() {
$assert_prop
    }
}
