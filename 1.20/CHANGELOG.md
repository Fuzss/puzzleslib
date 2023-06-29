# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v8.0.1-1.20.1] - 2023-06-29
### Added
- Added `DispenseBehaviorsContext`
### Changed
- `ModConstructor::onCommonSetup` and `ClientModConstructor::onClientSetup` now always run deferred on Forge, eliminating the previous `ModLifecycleContext` argument
### Fixed
- A few possible `java.util.ConcurrentModificationException`s during start-up are now prevented by running setup tasks deferred on Forge

## [v8.0.0-1.20.1] - 2023-06-25
- Ported to Minecraft 1.20.1

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
