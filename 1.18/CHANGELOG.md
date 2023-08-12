# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v3.5.7-1.18.2] - 2023-08-12
### Fixed
- Fixed start-up crash when the [Emojiful](https://www.curseforge.com/minecraft/mc-mods/emojiful) mod is installed

## [v3.5.6-1.18.2] - 2023-08-12
### Changed
- Slightly reworked `DeferredTooltipRendering`
### Fixed
- Fixed implementation errors in `UseItemEvents.Tick` on Fabric which would prevent successfully using items like bows and tridents

## [v3.5.5-1.18.2] - 2023-08-12
### Fixed
- Fixed `LivingDeathCallback` not firing for all living entities

## [v3.5.4-1.18.2] - 2023-08-12
### Added
- Added `DeferredTooltipRendering`

## [v3.5.3-1.18.2] - 2023-08-11
### Fixed
- Fixed mixins from the common module not applying

## [v3.5.2-1.18.2] - 2023-08-11
### Added
- Added `TagProviderExtension`

## [v3.5.1-1.18.2] - 2023-08-11
### Fixed
- Fixed crash on Forge due to invalid accessor mixin
- Fixed creative tabs not being built during data gen
- Fixed `AbstractTagProvider` containing an unintended abstract method

## [v3.5.0-1.18.2] - 2023-08-10
- Parity with Puzzles Lib v8.0.18

## [v3.4.7-1.18.2] - 2023-08-09
### Changed
- Rework internal creative mode tab handling to prevent dedicated Fabric servers from crashing, to allow mod items to show in JEI/REI and to greatly reduce the amount of mixins required for the implementation

## [v3.4.6-1.18.2] - 2023-06-20
### Fixed
- Fixed a class loading issue related to loading service provider interfaces

## [v3.4.5-1.18.2] - 2023-06-05
### Fixed
- Fixed crash with [Tropicraft](https://legacy.curseforge.com/minecraft/mc-mods/tropicraft) mod when creating or joining a world

## [v3.4.4-1.18.2] - 2023-06-04
### Fixed
- Fixed mod items missing from creative mode search again

## [v3.4.3-1.18.2] - 2023-06-04
### Fixed
- Fixed `OutOfMemoryError` due to infinite recursion during biome loading

## [v3.4.2-1.18.2] - 2023-06-04
### Fixed
- Fixed mod items missing from creative mode search
- Fixed biome modifications crashing on Forge

## [v3.4.1-1.18.2] - 2023-06-04
### Added
- Added more transitive access wideners

## [v3.4.0-1.18.2] - 2023-06-03
- Backported to Minecraft 1.18.2

## [v3.3.6-1.18.2] - 2023-02-26
### Fixed
- Fixed a bug where deserializing a list of ids always yields nothing if the ids don't use the `minecraft` namespace

## [v3.3.5-1.18.2] - 2022-05-25
### Fixed
- Fixed configs not properly being loaded

## [v3.3.4-1.18.2] - 2022-05-24
### Fixed
- Prevent crash when externally registering additional config types for a mod

## [v3.3.3-1.18.2] - 2022-04-30
### Fixed
- Hopefully fixed rare crash on start-up due to some registry objects not having been updated

## [v3.3.2-1.18.2] - 2022-03-08
### Fixed
- Fixed a small issue that prevented registering entity based capabilities

## [v3.3.1-1.18.2] - 2022-03-03
### Fixed
- Fixed an issue where a wrong config load state would be set

## [v3.3.0-1.18.2] - 2022-03-03
- Updated to Minecraft 1.18.2
### Changed
- Made config system more aware of what parts have been loaded yet to avoid accidentally accessing data too early

## [v3.2.1-1.18.1] - 2022-02-25
### Added
- Added tooltip utility methods for checking if certain modifier keys are pressed

## [v3.2.0-1.18.1] - 2022-02-22
### Added
- Added system for managing Forge's capabilities

## [v3.1.5-1.18.1] - 2022-02-10
### Fixed
- Made it more clear when category comments in a config are not supported

## [v3.1.4-1.18.1] - 2022-02-09
### Fixed
- Hopefully fixed an issue where registry entries rarely wouldn't load properly, also added a bunch more log output for debugging in case something goes wrong registering content in the future
- Fixed an issue with some category comments being unable to apply

## [v3.1.3-1.18.1] - 2021-12-28
### Fixed
- Fixed an ordering issue when loading configs

## [v3.1.2-1.18.1] - 2021-12-24
### Added
- Added helper class for accessing hidden fields on screen instances

## [v3.1.1-1.18.1] - 2021-12-15
### Added
- Added more helper methods for registering sound events and items

## [v3.1.0-1.18.1] - 2021-12-13
- Compiled for Minecraft 1.18.1

## [v3.0.2-1.18] - 2021-12-10
### Added
- Added a helper method for converting a list of registry entries to string

## [v3.0.1-1.18] - 2021-12-05
### Added
- Added utility classes for working with json files
### Fixed
- Fixed a rare case where configs wouldn't load properly on start-up

## [v3.0.0-1.18] - 2021-12-01
- Ported to Minecraft 1.18

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/