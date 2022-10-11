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
${project.marytts.component.config.collect { key, value ->
    if (value instanceof List)
        "        assert config.properties.'$key" + ".list'.tokenize().containsAll(" + value.inspect() + ")"
    else
        "        assert config.properties.'$key' == '$value'"
}.join('\n')
        }
    }
}
