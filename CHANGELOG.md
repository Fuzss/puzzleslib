# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v5.0.23-1.19.3] - 2023-05-10
### Added
- Added a few new player related events

## [v5.0.22-1.19.3] - 2023-05-09
### Changed
- Small improvements to `ConfigDataSet`

## [v5.0.21-1.19.3] - 2023-05-08
### Added
- Added `LivingDeathCallback`

## [v5.0.20-1.19.3] - 2023-05-06
### Added
- Many new events, mainly support for context based events like events that only run for a specific screen instance
- Added support for registering data pack reload listeners
- Added access to the `mods` directory in common
### Changed
- Quilt is now properly recognized in the Fabric subproject

## [v5.0.19-1.19.3] - 2023-03-28
### Changed
- A few additions to `TooltipRenderHelper`

## [v5.0.18-1.19.3] - 2023-03-28
### Added
- Added `TooltipRenderHelper`

## [v5.0.17-1.19.3] - 2023-03-27
### Added
- Added some utilities for creating runtime generated data / resource packs

## [v5.0.16-1.19.3] - 2023-03-27
### Added
- Added support for dynamically adding new data / resource pack finders

## [v5.0.15-1.19.3] - 2023-03-26
### Added
- Added a few new events
### Fixed
- Fixed `NullPointerException` in `AbstractTagProvider$Items`

## [v5.0.14-1.19.3] - 2023-03-26
### Added
- Added a few new events
### Fixed
- Fixed `FarmlandTrample` not being accessible

## [v5.0.13-1.19.3] - 2023-03-26
### Added
- Added a few new events
- Added `MutableBoolean` and `DefaultedBoolean` data classes
### Fixed
- No longer breaks trading discount strikethrough bar on Fabric, which would display behind the old price instead of on top

## [v5.0.12-1.19.3] - 2023-03-25
### Added
- Added a few new events
- Added a helper method for checking if a mob is a boss mob

## [v5.0.11-1.19.3] - 2023-03-25
### Added
- Added `LivingDropsCallback`
### Fixed
- Fixed a crash when entities were about to drop their loot on Fabric

## [v5.0.10-1.19.3] - 2023-03-24
### Added
- Added some more helper methods to `AbstractLanguageProvider`

## [v5.0.9-1.19.3] - 2023-03-24
### Added
- Added a few new events
- Added some more helper methods to `AbstractModelProvider`
### Fixed
- Fixed `CreativeModeTab$Builder` instances not being built on Fabric

## [v5.0.8-1.19.3] - 2023-03-23
### Added
- Added a few new events
### Changed
- `ConfigDataSet` now implements `java.util.Collection`
- Minor refactors for `ModelLayerFactory`

## [v5.0.7-1.19.3] - 2023-03-22
### Added
- Added a few new events
- Added a helper method to `DynamicBakingCompletedContext` for retrieving baked models from a provided `ResourceLocation`
- Added `AbstractModelProvider` for generating block state and block / item model assets
### Changed
- Migrated command registration and loot table load events to new event api implementation

## [v5.0.6-1.19.3] - 2023-03-22
### Added
- Added a few new events
### Fixed
- Fixed client mod construction sometimes running before common mod construction on Forge
- Fixed a typo in `DefaultedFloat`

## [v5.0.5-1.19.3] - 2023-03-21
### Fixed
- Fixed a class loading order issue related to the execution of buildable mod-specific instances

## [v5.0.4-1.19.3] - 2023-03-21
### Added
- Added `ModContext` internal helper class for managing mod-specific instances
### Changed
- Move most config system code to common project
- `NetworkHandlerV3` and `ConfigHolder` are now built automatically at the appropriate time
### Fixed
- Fixed common publication containing an invalid `fabric.mod.json`

## [v5.0.3-1.19.3] - 2023-03-19
### Added
- Added helper method to `RegistryManager` for creating new instances of `TagKey`
### Changed
- Remove `UNSAFE` again for handling config annotations and use method handles instead

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
