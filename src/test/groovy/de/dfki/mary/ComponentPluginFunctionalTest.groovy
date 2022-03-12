package de.dfki.mary

import org.gradle.api.JavaVersion
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.testng.SkipException
import org.testng.annotations.BeforeGroups
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

class ComponentPluginFunctionalTest {

    GradleRunner gradle

    void setupGradleAndProjectDir(boolean createCustomFiles, String... resourceNames) {
        def projectDir = File.createTempDir()
        gradle = GradleRunner.create().withProjectDir(projectDir).withPluginClasspath().forwardOutput()
        resourceNames.each { resourceName ->
            new File(projectDir, resourceName).withWriter {
                it << this.class.getResourceAsStream(resourceName)
            }
        }
        if (createCustomFiles) {
            def resourceParent = new File(projectDir, 'src/main/resources/path/to/the')
            resourceParent.mkdirs()
            new File(resourceParent, 'fnord').createNewFile()
        }
    }

    @BeforeGroups(groups = 'default')
    void setupDefault() {
        setupGradleAndProjectDir(false, 'build-with-defaults.gradle', 'test-tasks.gradle')
    }

    @BeforeGroups(groups = 'custom')
    void setupCustom() {
        setupGradleAndProjectDir(true, 'customized-build.gradle', 'test-tasks.gradle', 'config.yaml')
    }

    @BeforeGroups(groups = 'custom-legacy-gradle')
    void setupCustomLegacyGradle() {
        setupGradleAndProjectDir(true, 'customized-build.gradle', 'test-tasks.gradle', 'config.yaml')
        gradle = gradle.withGradleVersion('5.1')
    }

    @DataProvider
    Object[][] taskNames() {
        // task name to run, and whether to chase it with a test task named "testName"
        [
                ['help', false],
                ['testPlugins', false],
                ['testExtension', false],
                ['testConfig', false],
                ['generateServiceLoader', true],
                ['generateSource', true],
                ['generateConfig', true],
                ['processResources', true],
                ['compileGroovy', true],
                ['compileTestGroovy', true],
                ['test', false],
                ['integrationTest', false],
                ['check', true]
        ]
    }

    void runGradleWithBuildFileAndTaskAndOptionalTestTask(String buildFileName, String taskName, boolean runTestTask) {
        def gradleArgs = ['--warning-mode', 'all', '--build-file', buildFileName]
        def result = gradle.withArguments(gradleArgs + [taskName]).build()
        assert result.task(":$taskName").outcome in [SUCCESS, UP_TO_DATE]
        if (runTestTask) {
            def testTaskName = 'test' + taskName.capitalize()
            result = gradle.withArguments(gradleArgs + [testTaskName]).build()
            assert result.task(":$taskName").outcome == UP_TO_DATE
            assert result.task(":$testTaskName").outcome == SUCCESS
        }
    }

    @Test(groups = 'default', dataProvider = 'taskNames')
    void defaultBuildTestTasks(String taskName, boolean runTestTask) {
        runGradleWithBuildFileAndTaskAndOptionalTestTask('build-with-defaults.gradle', taskName, runTestTask)
    }

    @Test(groups = 'custom', dataProvider = 'taskNames')
    void customBuildTestTasks(String taskName, boolean runTestTask) {
        runGradleWithBuildFileAndTaskAndOptionalTestTask('customized-build.gradle', taskName, runTestTask)
    }

    @Test(groups = 'custom-legacy-gradle', dataProvider = 'taskNames', expectedExceptions = UnexpectedBuildFailure.class)
    void customLegacyGradleBuildTestTasks(String taskName, boolean runTestTask) {
        try {
            runGradleWithBuildFileAndTaskAndOptionalTestTask('customized-build.gradle', taskName, runTestTask)
        } catch (all) {
            if (JavaVersion.current() > JavaVersion.VERSION_12)
                throw new SkipException(all.message)
            else
                throw all
        }
    }
}
