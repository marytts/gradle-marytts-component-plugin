package de.dfki.mary

import org.gradle.testfixtures.ProjectBuilder
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class ComponentDataTest {

    ComponentData componentData

    @BeforeMethod
    void setUp() {
        def project = ProjectBuilder.builder().build()
        this.componentData = new ComponentData(project)
    }

    @Test
    void 'Given name, When defaults are used, Then value is correct'() {
        assert componentData.name == 'MyComponent'
    }

    @Test
    void 'Given name, When customized, Then value is correct'() {
        def name = 'CustomComponent'
        componentData.name = name
        assert componentData.name == name
    }

    @Test
    void 'Given packageName, When defaults are used, Then value is correct'() {
        assert componentData.packageName == 'mypackage'
    }

    @Test
    void 'Given packageName, When customized, Then value is correct'() {
        def packageName = 'my.custom.package'
        componentData.packageName = packageName
        assert componentData.packageName == packageName
    }

    @Test
    void 'Given packagePath, When defaults are used, Then value is correct'() {
        assert componentData.packagePath == 'mypackage'
    }

    @Test
    void 'Given packagePath, When custom packageName is set, Then value is correct'() {
        componentData.packageName = 'my.custom.package'
        assert componentData.packagePath == 'my/custom/package'
    }

    @Test
    void 'Given config, When defaults are used, value is correct'() {
        assert componentData.config == [:]
    }

    @Test
    void 'Given config, When customized, Then value is correct'() {
        def config = [
                locale     : 'xy',
                'foo.bar'  : 'foo baz',
                'foo.qux'  : ['quux', 'quuux'],
                'foo.fnord': 'jar:/path/to/the/fnord'
        ]
        componentData.config = config
        assert componentData.config == config
    }

    @Test
    void 'Given configBaseClass, When defaults are used, Then value is correct'() {
        assert componentData.configBaseClass == 'MaryConfig'
    }

    @Test
    void 'Given configBaseClass, When customized, Then value is correct'() {
        def configBaseClass = 'MyCustomConfigClass'
        componentData.configBaseClass = configBaseClass
        assert componentData.configBaseClass == configBaseClass
    }
}
