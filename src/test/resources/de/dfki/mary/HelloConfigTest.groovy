package my.world

import marytts.config.*
import org.testng.annotations.*

class HelloConfigTest {

    HelloConfig config

    @BeforeMethod
    void setup() {
        config = new HelloConfig()
    }

    @Test
    public void isNotMainConfig() {
        assert config.isMainConfig() == false
    }

    @Test
    public void testConfigBaseClass() {
        assert config instanceof LanguageConfig
    }

    @Test
    public void canGetProperties() {
        assert config.properties.'locale' == 'xy'
        assert config.properties.'foo.bar' == 'foo baz'
        assert config.properties.'foo.qux.list'.tokenize().containsAll(['quux', 'quuux'])
        assert config.properties.'foo.fnord' == 'jar:/path/to/the/fnord'
    }
}
