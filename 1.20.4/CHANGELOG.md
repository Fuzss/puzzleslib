# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v20.4.20-1.20.4] - 2024-02-10
### Fixed
- Fix type parameter on `RegistryManager::registerEntityDataSerializer`

## [v20.4.19-1.20.4] - 2024-02-09
### Added
- Add `RegistryManager::registerEntityDataSerializer`

## [v20.4.18-1.20.4] - 2024-02-04
### Added
- Add `GuiGraphicsHelper`
- Add `DynamicallyCopiedPackResources`
### Changed
- Allow `SpritelessImageButton` to support drawing the vanilla button background
- `CapabilityToken` no longer needs to be set for a capability on Forge
### Fixed
- Fix `ContainerScreenEvents$Background` running too late on Fabric (Forge & NeoForge are unfortunately still broken, waiting to be fixed by the respective loader)

## [v20.4.17-1.20.4] - 2024-02-03
### Fixed
- Fix `ClientModConstructor::onRegisterRenderBuffers` running too late on Forge

## [v20.4.16-1.20.4] - 2024-02-03
### Added
- Add `Proxy::splitTooltipLines`

## [v20.4.15-1.20.4] - 2024-02-01
### Fixed
- Fix crash when breaking crops with Farmer's Delight's sickle

## [v20.4.14-1.20.4] - 2024-01-30
### Changed
- Update to latest mod loader versions
### Fixed
- Fix start-up crash on NeoForge
- Fix mouse drag event not firing on Forge

## [v20.4.13-1.20.4] - 2024-01-29
### Added
- Add `AbstractAdvancementProvider`
### Fixed
- Fix copy tag recipes sometimes using an incorrect serializer

## [v20.4.12-1.20.4] - 2024-01-29
### Fixed
- Fix `AbstractLootProvider$EntityTypes`

## [v20.4.11-1.20.4] - 2024-01-28
### Fixed
- Fix broken data generation again

## [v20.4.10-1.20.4] - 2024-01-28
### Added
- Add `NeoForgeCapabilityHelperV2`
- Add `ModLoaderEnvironment::isDataGeneration`
### Fixed
- Fix development environment utilities breaking data generation

## [v20.4.9-1.20.4] - 2024-01-27
### Added
- Add `EditBox` improvements for development environments
### Fixed
- Fix `TagsUpdatedCallback` & `ServerLifecycleEvents.Starting` not running on dedicated servers

## [v20.4.8-1.20.4] - 2024-01-26
### Added
- Add `ClientModConstructor::onRegisterRenderBuffers`
- A lot more small utilities for development environments
### Changed
- Reimplement capability system on Fabric on top of Fabric Api's attachment api in favor of the Cardinal Components library
- Implement `LivingConversionCallback` using Fabric Api's new native event
- Implement `MenuScreensContext` using NeoForge's new native event

## [v20.4.7-1.20.4] - 2024-01-24
### Changed
- Revert reverted reduced retina resolution
### Fixed
- Fix `ScreenMouseEvents$BeforeMouseDrag` & `ScreenMouseEvents$AfterMouseDrag` not firing on Forge & NeoForge

## [v20.4.6-1.20.4] - 2024-01-23
### Changed
- Some more adjustments to `ScreenTooltipFactory`
- Revert reduced retina resolution

## [v20.4.5-1.20.4] - 2024-01-23
### Added
- Add `NeoForgeCapabilityHelper`
- Add `CommandOverrides`
### Changed
- Some adjustments to `ScreenTooltipFactory`
- Set default options and run default commands for development environments
- Reduce retina resolution during development on Mac (Fabric only)

## [v20.4.4-1.20.4] - 2024-01-21
### Added
- Add `GameRuleValueOverrides`
### Changed
- Migrate all accessor mixins in common to access wideners
- Set some default game rule values for development environments
- Handle invalidated capabilities on Forge by supplying a temporary default instance
- Allow data generation run configuration to exit properly

## [v20.4.3-1.20.4] - 2024-01-20
### Added
- Add `SpritelessImageButton`
- Add `NbtSerializable`
- Add `RegistryHelper::getBuiltInRegistryHolder`

## [v20.4.2-1.20.4] - 2024-01-20
### Added
- Add `MenuScreensContext` for registering menu screen factories
### Changed
- Allow `EnumProvider` to accept a custom namespace

## [v20.4.1-1.20.4] - 2024-01-19
### Added
- Add `KeyedValueProvider` for serializing values not from a registry in `ConfigDataSet`
### Changed
- Revert `RegistryManager` returning `Holder` instead of `Holder.Reference`
- Migrate `init.v2` to `init.v3`
- Update some javadoc
- Turn some methods public in data providers
### Fixed
- Fix unable to instantiate `AbstractRecipeProvider` on NeoForge due to an additional constructor parameter being patched in
### Removed
- Remove `Proxy::getKeyMappingComponent`

## [v20.4.0-1.20.4] - 2024-01-17
- Ported to Minecraft 1.20.4
- Ported to NeoForge
- Support Mixin Extras
### Changed
- Overhauled capability api as `v3`, `v2` has been removed
- Renamed `RegistryManagerV3` to `RegistryManager`, now uses `Holder` instead of `Holder.Reference`
- Restructured event implementations, also delayed event loading to avoid being blamed for class loading errors
- Data generation has been exclusively moved to NeoForge
- Minor overhauls to `NetworkHandlerV2` regarding `Message` registration
### Removed
- Removed `RegistryManagerV2`
- Removed bundled PuzzlesAccessApi in favor of Loom's access wideners
