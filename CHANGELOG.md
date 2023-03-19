# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v5.0.2-1.19.3] - 2023-03-19
### Fixed
- Fix access transformer remapping failing in common project

## [v5.0.1-1.19.3] - 2023-03-19
### Changed
- Fabric Api now is an api dependency for the common project to allow access wideners to apply automatically

## [v5.0.0-1.19.3] - 2023-03-16
- Ported to Minecraft 1.19.3
### Added
- Added an experimental api for calling events in the common project, Forge events and Fabric callbacks are wrapped accordingly
- Added new registration and helper methods for handling `CreativeModeTab`
- Added a bunch of default data generators
### Changed
- Split codebase into `api`, `impl` and `mixin` subpackages
- `NetworkHandlerV3` instances must now call `#initialize` to prevent message registration running too early
- The [Cardinal Components](https://www.curseforge.com/minecraft/mc-mods/cardinal-components) library is no longer embedded, mods that depend on it need to handle it themselves now
- Removed wrapper classes for Forge's config api in common, Forge Config API Port native common distribution is now used instead
- Annotated config values must now be `final`, they are now set via `UNSAFE` instead of reflection
- `fuzs.puzzleslib.api.client.core.v1.ClientModConstructor#onRegisterModelBakingCompletedListeners` no longer bakes models, vanilla now does this automatically, the event has also been split to account for changes in vanilla's model manager
### Removed
- Removed a bunch of deprecated methods, mainly in `fuzs.puzzleslib.api.client.core.v1.ClientModConstructor`
- Removed a few methods for accessing built-in registry data in the biome api since they no longer seem to be supported in vanilla
- Removed some outdated helper methods
- Removed `fuzs.puzzleslib.client.core.ClientModConstructor#onRegisterAtlasSprites` in favor of vanilla's new json based system

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
