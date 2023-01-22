Gradle MaryTTS Component Plugin
===============================

[v0.3.1] - 2023-01-22
---------------------

### Added

- Group to all tasks

### Fixed

- Downstream failures when wrong template unpacking configuration produced `null` source files
- Failures in generated integration tests, when non-namespaced `name` property is loaded from other MaryConfigs

### Changed

- [all changes since v0.3.0]

[v0.3.0] - 2022-11-23
---------------------

### Added

- Testing on Java 8, 11, 17

### Removed

- Runtime Groovy dependency
- Reliance on Bintray/JCenter

### Changed

- Build with Gradle v7.5.1
- Upgraded plugins, dependencies
- Migrated from Travis CI to GitHub Actions
- Upgraded MaryTTS to v5.2.1
- Updated documentation
- Migrated publishing from OJO to OSSRH
- [all changes since v0.2.2]

[v0.2.2] - 2020-06-26
---------------------

### Adding

- Publishing POM metadata

### Changed

- Build with Gradle v6.4
- Use Groovy templating to generate sources
- [all changes since v0.2.1]

[v0.2.1] - 2020-04-15
---------------------

### Added

- Also test against legacy Gradle v5.1

### Changed

- Document plugin behavior
- Set default compatibility to Java 8
- [all changes since v0.2]

[v0.2] - 2020-04-10
-------------------

### Changed

- Test on OpenJDK 8 through 13
- Build with Gradle v6.3
- Update dependencies
- [all changes since v0.1]

[v0.1] - 2018-07-30
-------------------

Initial release

[v0.3.1]: https://github.com/marytts/gradle-marytts-component-plugin/releases/tag/v0.3.1
[all changes since v0.3.0]: https://github.com/marytts/gradle-marytts-component-plugin/compare/v0.3.0...v0.3.1
[v0.3.0]: https://github.com/marytts/gradle-marytts-component-plugin/releases/tag/v0.3.0
[all changes since v0.2.2]: https://github.com/marytts/gradle-marytts-component-plugin/compare/v0.2.2...v0.3.0
[v0.2.2]: https://github.com/marytts/gradle-marytts-component-plugin/releases/tag/v0.2.2
[all changes since v0.2.1]: https://github.com/marytts/gradle-marytts-component-plugin/compare/v0.2.1...v0.2.2
[v0.2.1]: https://github.com/marytts/gradle-marytts-component-plugin/releases/tag/v0.2.1
[all changes since v0.2]: https://github.com/marytts/gradle-marytts-component-plugin/compare/v0.2.1...v0.2
[v0.2]: https://github.com/marytts/gradle-marytts-component-plugin/releases/tag/v0.2
[all changes since v0.1]: https://github.com/marytts/gradle-marytts-component-plugin/compare/v0.1...v0.2
[v0.1]: https://github.com/marytts/gradle-marytts-component-plugin/releases/tag/v0.1
