package my.world

import marytts.server.MaryProperties
import marytts.util.MaryRuntimeUtils

import org.testng.annotations.*

class LoadHelloIT {

    @BeforeMethod
    void setup() {
        MaryRuntimeUtils.ensureMaryStarted()
    }

    @DataProvider
    Object[][] properties() {
        [
            ['foo.bar', 'foo baz'],
            ['foo.qux.list', ['quux', 'quuux']],
            ['foo.fnord', 'jar:/path/to/the/fnord']
        ]
    }

    @Test(dataProvider = 'properties')
    public void canGetProperty(name, expected) {
        def actual
        switch (name) {
            case 'name':
                break
            case ~/.+\.list$/:
                actual = MaryProperties.getList(name)
                assert actual.containsAll(expected)
                break
            default:
                actual = MaryProperties.getProperty(name)
                assert expected == actual
                break
        }
        if ("$expected".startsWith('jar:')) {
            assert MaryProperties.getStream(name)
        }
    }
}
