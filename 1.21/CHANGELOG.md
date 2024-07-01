# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v20.4.52-1.20.4] - 2024-06-16
### Fixed
- Attempt fix remapping for Forge

## [v20.4.51-1.20.4] - 2024-06-11
### Fixed
- Fix `RangedSliderButton::getValue` again

## [v20.4.50-1.20.4] - 2024-06-11
### Fixed
- Fix `RangedSliderButton::getValue`

## [v20.4.49-1.20.4] - 2024-06-11
### Added
- Add `RangedSliderButton`

## [v20.4.48-1.20.4] - 2024-06-07
### Added
- Add `ComponentHelper::toString`

## [v20.4.47-1.20.4] - 2024-06-07
### Added
- Add `ComputeFieldOfViewCallback`
### Changed
- Add additional parameters for `GatherDebugTextEvents` and move injection point on Fabric 

## [v20.4.46-1.20.4] - 2024-06-03
### Changed
- Load client and common configs immediately upon registration on Forge & NeoForge to prepare for startup configs on NeoForge in 1.20.6 and for parity with Fabric 
### Fixed
- Fix overloads in `MutableSoundType`
- Fix `PlayerTrackingEvents#START` running too early on Fabric by introducing a custom event internally

## [v20.4.45-1.20.4] - 2024-06-01
### Added
- Add `HolderBackedSoundType` and `MutableSoundType`
### Fixed
- Fix `PlayerInteractEvents$AttackBlock` not firing for creative players on Fabric

## [v20.4.44-1.20.4] - 2024-05-26
### Fixed
- Attempt to improve handling for missing child reload listeners during resource reloading

## [v20.4.43-1.20.4] - 2024-05-09
### Fixed
- Fix crash involving `GatherPotentialSpawnsCallback` on NeoForge and when the ModernFix mod is installed on Forge

## [v20.4.42-1.20.4] - 2024-03-24
### Added
- Add `LookingAtEndermanCallback`
### Changed
- Adjust implementation of `PlayerInteractEvents` to allow for returning `InteractionResult#PASS` without the value being ignored
- Make `LoadCompleteCallback` run sequentially on Forge & NeoForge
### Fixed
- Fix `EntityDataSerializer` registration on Forge to avoid id mismatches when receiving server data

## [v20.4.41-1.20.4] - 2024-03-19
### Added
- Add `RenderTooltipCallback` in favor of `ScreenTooltipEvents$Render`
### Fixed
- Fix `LivingDeathCallback` not running for players on Fabric

## [v20.4.40-1.20.4] - 2024-03-18
### Fixed
- Fix mod list on Forge & NeoForge unable to handle mod jars that define multiple mods at once in `mods.toml`

## [v20.4.39-1.20.4] - 2024-03-14
### Changed
- Call `CapabilityComponent::setChanged` when syncing to remotes

## [v20.4.38-1.20.4] - 2024-03-12
### Added
- Add `ContainerEvents`

## [v20.4.37-1.20.4] - 2024-03-12
### Added
- Add `CapabilityComponent::initialize`

## [v20.4.36-1.20.4] - 2024-03-02
### Changed
- Expand and simplify implementation of `AbstractTagAppender`

## [v20.4.35-1.20.4] - 2024-03-01
### Added
- Add new version of `AbstractTagProvider`
- Add custom `AbstractTagAppender`

## [v20.4.34-1.20.4] - 2024-02-29
### Changed
- Move `LoadCompleteCallback` to run much earlier on dedicated servers on Fabric

## [v20.4.33-1.20.4] - 2024-02-28
### Changed
- Update some `DynamicPackResources` internals

## [v20.4.32-1.20.4] - 2024-02-27
### Added
- Add `ModLoaderEnvironment::getCurrentMappingsNamespace`
### Changed
- Allow setting default pack position in `PackResourcesHelper`

## [v20.4.31-1.20.4] - 2024-02-24
### Fixed
- Fix player capabilities providing an invalid holder when the player is dead on Forge

## [v20.4.30-1.20.4] - 2024-02-23
### Fixed
- Fix `RegisterCommandsCallback` not running on Fabric

## [v20.4.29-1.20.4] - 2024-02-23
### Fixed
- Fix `RenderHandEvents#OFF_HAND` firing the wrong hand event type 

## [v20.4.28-1.20.4] - 2024-02-23
### Added
- Add `RegistryManager::registerArgumentType`
- Add `RenderHandEvents` in favor of `RenderHandCallback`
### Fixed
- Fix clients attempting to synchronize changed capability values

## [v20.4.27-1.20.4] - 2024-02-22
### Added
- Add `KeyActivationHandler`
- Add `KeyMappingHelper::registerKeyMapping`
- Add `PlayerSet`

## [v20.4.26-1.20.4] - 2024-02-19
### Changed
- Deprecate `ScreenHelper` in favor of Access Wideners and `ScreenHelperV2`

## [v20.4.25-1.20.4] - 2024-02-19
### Fixed
- Fix minecart type registry map running out of bounds

## [v20.4.24-1.20.4] - 2024-02-18
### Added
- Add `MinecartTypeRegistry`
- Add `ContainerMenuHelper`
- Add `NeoForgeCapabilityHelperV2#registerEntityContainer`

## [v20.4.23-1.20.4] - 2024-02-15
### Fixed
- Fix dedicated server crash due to faulty import

## [v20.4.22-1.20.4] - 2024-02-14
### Added
- Add `GenericExplosionHelper`
- Add `AbstractLootProviderV2`
- Add additional helper methods to `AbstractSoundDefinitionProvider`
- Add `AbstractLanguageProvider::addSpawnEgg`
### Changed
- Validate item models in `AbstractModelProvider`
### Fixed
- Fix `AbstractBuiltInDataProvider` not registering generated resources with the file helper
- Fix `ItemDisplayOverrides` crashing on Forge & NeoForge

## [v20.4.21-1.20.4] - 2024-02-10
### Fixed
- Fix `RegistryManager::registerEntityDataSerializer` not actually registering the serializer on Fabric & Forge

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
