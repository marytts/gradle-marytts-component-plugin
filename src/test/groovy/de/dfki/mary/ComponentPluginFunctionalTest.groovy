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

    void setupGradleAndProjectDir(boolean createCustomFiles, String buildScriptResourceName, String... resourceNames) {
        def projectDir = File.createTempDir()
        new File(projectDir, 'settings.gradle').createNewFile()
        gradle = GradleRunner.create().withProjectDir(projectDir).withPluginClasspath().forwardOutput()
        new File(projectDir, 'build.gradle').withWriter {
            it << this.class.getResourceAsStream(buildScriptResourceName)
        }
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
        setupCustom()
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
                ['unpackSourceTemplates', true],
                ['unpackTestSourceTemplates', true],
                ['unpackIntegrationTestSourceTemplates', true],
                ['generateSource', true],
                ['generateTestSource', true],
                ['generateIntegrationTestSource', true],
                ['generateConfig', true],
                ['processResources', true],
                ['compileJava', true],
                ['compileTestGroovy', true],
                ['test', false],
                ['integrationTest', false],
                ['check', true]
        ]
    }

    void runGradleWithBuildFileAndTaskAndOptionalTestTask(String taskName, boolean runTestTask) {
        def gradleArgs = ['--warning-mode', 'all']
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
        runGradleWithBuildFileAndTaskAndOptionalTestTask(taskName, runTestTask)
    }

    @Test(groups = 'custom', dataProvider = 'taskNames')
    void customBuildTestTasks(String taskName, boolean runTestTask) {
        runGradleWithBuildFileAndTaskAndOptionalTestTask(taskName, runTestTask)
    }

    @Test(groups = 'custom-legacy-gradle', dataProvider = 'taskNames', expectedExceptions = UnexpectedBuildFailure.class)
    void customLegacyGradleBuildTestTasks(String taskName, boolean runTestTask) {
        try {
            runGradleWithBuildFileAndTaskAndOptionalTestTask(taskName, runTestTask)
        } catch (all) {
            if (JavaVersion.current() > JavaVersion.VERSION_12)
                throw new SkipException(all.message)
            else
                throw all
        }
    }
}
