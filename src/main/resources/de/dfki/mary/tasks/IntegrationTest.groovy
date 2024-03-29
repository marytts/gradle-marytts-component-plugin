package $project.marytts.component.packageName

import marytts.server.MaryProperties
import marytts.util.MaryRuntimeUtils

import org.testng.annotations.*

class Load${project.marytts.component.name}IT {

    @BeforeMethod
    void setup() {
        MaryRuntimeUtils.ensureMaryStarted()
    }

    @DataProvider
    Object[][] properties() {
        [
${project.marytts.component.config.collect { key, value ->
    if (value instanceof List)
        "            ['$key" + ".list', " + value.inspect() + "]"
    else
        "            ['$key', '$value']"
    }.findAll {!it.contains('locale') }.join(',\n')
}
        ]
    }

    @Test(dataProvider = 'properties')
    public void canGetProperty(name, expected) {
        def actual
        switch (name) {
            case 'name':
                break
            case ~/.+\\.list\$/:
                actual = MaryProperties.getList(name)
                assert actual.containsAll(expected)
                break
            default:
                actual = MaryProperties.getProperty(name)
                assert expected == actual
                break
        }
        if ("\$expected".startsWith('jar:')) {
            assert MaryProperties.getStream(name)
        }
    }
}
