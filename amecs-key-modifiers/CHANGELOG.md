# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Support for Minecraft 26.1

### Fixed

- Fix key modifiers of modded key bindings not saving correctly.
- Fixed a crash when comparing key modifier combinations with different internal lengths.
  This can happen when the modifier combinations are constructed at different times during
  the modifier registration process. (#112)


## [1.0.1] - 2026-03-16

### Fixed

- Fixed persisted key modifiers in the new format not being read in correctly. (#111)
- Fixed legacy dummy mod being exposed as a Maven dependency.


## [1.0.0] - 2026-01-25

### Changed

- Extracted from the old Amecs' API library.

