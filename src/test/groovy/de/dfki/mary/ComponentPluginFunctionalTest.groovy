package de.dfki.mary

import org.gradle.testkit.runner.GradleRunner
import org.testng.annotations.*

import static org.gradle.testkit.runner.TaskOutcome.*

class ComponentPluginFunctionalTest {

    GradleRunner gradle

    @BeforeGroups(groups = 'default')
    void setupDefault() {
        def projectDir = File.createTempDir()
        gradle = GradleRunner.create().withProjectDir(projectDir).withPluginClasspath()
        ['build-with-defaults.gradle', 'test-tasks.gradle'].each { resourceName ->
            new File(projectDir, resourceName).withWriter {
                it << this.class.getResourceAsStream(resourceName)
            }
        }
    }

    @BeforeGroups(groups = 'custom')
    void setupCustom() {
        def projectDir = File.createTempDir()
        gradle = GradleRunner.create().withProjectDir(projectDir).withPluginClasspath()
        ['customized-build.gradle', 'test-tasks.gradle'].each { resourceName ->
            new File(projectDir, resourceName).withWriter {
                it << this.class.getResourceAsStream(resourceName)
            }
        }
    }

    @DataProvider
    Object[][] taskNames() {
        // task name to run, and whether to chase it with a test task named "testName"
        [
                ['help', false],
                ['testPlugins', false],
                ['testExtension', false],
                ['generateSource', true],
                ['generateConfig', true],
                ['processResources', true],
                ['compileGroovy', true],
                ['compileTestGroovy', true],
                ['test', false],
                ['integrationTest', false]
        ]
    }

    @Test(groups = 'default', dataProvider = 'taskNames')
    void defaultBuildTestTasks(String taskName, boolean runTestTask) {
        def result = gradle.withArguments('--info', '--build-file', 'build-with-defaults.gradle', taskName).build()
        println result.output
        assert result.task(":$taskName").outcome in [SUCCESS, UP_TO_DATE]
        if (runTestTask) {
            def testTaskName = 'test' + taskName.capitalize()
            result = gradle.withArguments('--info', '--build-file', 'build-with-defaults.gradle', testTaskName).build()
            println result.output
            assert result.task(":$taskName").outcome == UP_TO_DATE
            assert result.task(":$testTaskName").outcome == SUCCESS
        }
    }

    @Test(groups = 'custom', dataProvider = 'taskNames')
    void customBuildTestTasks(String taskName, boolean runTestTask) {
        def result = gradle.withArguments('--info', '--build-file', 'customized-build.gradle', taskName).build()
        println result.output
        assert result.task(":$taskName").outcome in [SUCCESS, UP_TO_DATE]
        if (runTestTask) {
            def testTaskName = 'test' + taskName.capitalize()
            result = gradle.withArguments('--info', '--build-file', 'customized-build.gradle', testTaskName).build()
            println result.output
            assert result.task(":$taskName").outcome == UP_TO_DATE
            assert result.task(":$testTaskName").outcome == SUCCESS
        }
    }
}
