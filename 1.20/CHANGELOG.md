# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v8.0.14-1.20.1] - 2023-07-30
### Fixed
- Fixed an issue with the `Sheets` class accidentally being accessed too early, preventing modded materials such as wood types from registering correctly, which would lead to crashes when e.g. rendering signs

## [v8.0.13-1.20.1] - 2023-07-26
### Added
- Added `ScreenTooltipFactory`

## [v8.0.12-1.20.1] - 2023-07-23
### Fixed
- Fixed a crash when calling `ModelEvents` on Forge

## [v8.0.11-1.20.1] - 2023-07-23
### Added
- Added `RenderHighlightCallback`
- Added `RenderLevelEvents`
### Changed
- Shifted injection points for `InventoryMobEffectsCallback` to be compatible with the [Just Enough Items](https://www.curseforge.com/minecraft/mc-mods/jei) mod on Fabric
- Slightly changed some injection point shifts related to `AnimalTameCallback`

## [v8.0.10-1.20.1] - 2023-07-15
### Added
- Added `ScreenTooltipEvents.Render`
- Added two helper methods for getting `mouseX` and `mouseY` to `ScreenHelper`

## [v8.0.9-1.20.1] - 2023-07-12
### Added
- Added `RenderBlockOverlayCallback`
- Added `FogEvents$Render` and `FogEvents$ComputeColor`
### Fixed
- Fixed `ModelEvents` crashing on Fabric

## [v8.0.8-1.20.1] - 2023-07-06
### Changed
- Migrate `ModelEvents` to common event system

## [v8.0.7-1.20.1] - 2023-07-02
### Fixed
- Fixed a sound related crash on Fabric

## [v8.0.6-1.20.1] - 2023-07-02
### Fixed
- Fixed new mod loading architecture not allowing for multiple config handlers per mod

## [v8.0.5-1.20.1] - 2023-07-02
### Fixed
- Fixed a crash when rendering items on Fabric

## [v8.0.4-1.20.1] - 2023-07-01
### Changed
- Once again invert `ItemDisplayContext`s when registering item model perspective overrides

## [v8.0.3-1.20.1] - 2023-07-01
### Added
- Added `LoadCompleteCallback`
### Changed
- Internal changes to allow for more versatile mod loading
- Separate item model perspective now use built-in features on Forge and longer require a core mod there

## [v8.0.2-1.20.1] - 2023-06-29
### Fixed
- Re-upload to fix failed reobfuscation on Forge

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
