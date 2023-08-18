# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v8.0.25-1.20.1] - 2023-08-17
### Added
- Added `InteractionInputEvents$AttackV2`, `InteractionInputEvents$Use` and `InteractionInputEvents$Pick`
- Added `PlayerInteractEvents$AttackBlockV2` and `PlayerInteractEvents$UseItemV2`
- Added a few new helper methods to `ScreenHelper`
### Changed
- Cleaned up small parts to the event system to reduce duplicate code
- Revised implementation of all `PlayerInteractEvents` to match much more closely between Forge and Fabric
- `ServerEntityLevelEvents$LoadV2` now fires for all loaded entities, not just when they are loaded from chunk storage, but also when they are freshly spawned in
### Fixed
- Fixed brewing recipes not showing in some recipe viewer mods on Forge

## [v8.0.24-1.20.1] - 2023-08-16
### Changed
- Reworked implementation of `LivingDropsCallback` on Fabric to make it less likely for loot modifications from other mods to completely break all loot for a mob
### Fixed
- Fixed `ServiceProviderHelper` not loading as early as it should
- Removed debug code related to testing out new key binding features

## [v8.0.23-1.20.1] - 2023-08-14
### Added
- Added `CommonAbstractions::getMobSpawnType`
- Some refactors to `ServerEntityLevelEvents`, including the addition of `ServerEntityLevelEvents$Spawn`
### Fixed
- Fixed `ConfigDataSet` failing to dissolve when duplicate values are present

## [v8.0.22-1.20.1] - 2023-08-14
### Added
- Added `GatherPotentialSpawnsCallback`
- Added `ServerEntityLevelEvents$LoadV2`, `ServerEntityLevelEvents$Load` is now deprecated
- Added `CheckMobDespawnCallback`
- Added `EntityRidingEvents$Start` and `EntityRidingEvents$Stop`
- Added `CoreShadersContext` to `ClientModConstructor` for registering built-in shaders
- Added `KeyActivationContext` to helper with registering keybinds that only work when a screen is open or while playing (no screen is open)
### Changed
- Overhauled `GameRulesFactory` to add many convenient overloads, as well as cleaning up the Forge implementation
- A few new methods in `AbstractLanguageProvider` for creative tabs and game rules
- Adjusted `ScreenTooltipFactory` to no longer require a `Font` instance
- Refactored `KeyMappingsContext` to require a `KeyActivationContext` instance
- Reworked methods for registering new `PoiType`s in `RegistryManager` to no longer require a custom `PoiTypeBuilder` instance
- Migrate some internals on Fabric to Fabric Api's new model loading api
### Fixed
- Fixed `ScreenTooltipFactory` removing empty lines from tooltips

## [v8.0.21-1.20.1] - 2023-08-12
### Fixed
- Fixed start-up crash when the [Emojiful](https://www.curseforge.com/minecraft/mc-mods/emojiful) mod is installed

## [v8.0.20-1.20.1] - 2023-08-12
### Fixed
- Fixed implementation errors in `UseItemEvents.Tick` on Fabric which would prevent successfully using items like bows and tridents

## [v8.0.19-1.20.1] - 2023-08-11
### Added
- Added `TagProviderExtension`

## [v8.0.18-1.20.1] - 2023-08-10
### Added
- Added `GameRenderEvents.Before` and `GameRenderEvents.After`
- Added helper methods to `ClientAbstractions` for getting `partialTick` and `SearchRegistry`
### Changed
- Deprecated `SearchRegistryContext`

## [v8.0.17-1.20.1] - 2023-08-04
### Changed
- Custom smithing upgrade recipes without a smithing template are now automatically turned into crafting recipes with the same items and functionality to be visible in recipe viewers like JEI without having to provide dedicated support

## [v8.0.16-1.20.1] - 2023-08-01
### Fixed
- Fixed `LivingHurtCallback` not firing for players on Fabric
- Fixed `UseItemEvents$Tick` always having a wrong `useDuration` value
- Fixed `PlayerInteractEvents$UseBlock` and `PlayerInteractEvents$UseItem` not sending packets to the server for successful interactions if a result other than `InteractionResult#SUCCESS` is returned from the callback

## [v8.0.15-1.20.1] - 2023-07-30
### Fixed
- Reworked `ServerEntityLevelEvents$Load` and `ClientEntityLevelEvents$Load` on Fabric to prevent a potential memory leak when trying to prevent an entity from loading in

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
