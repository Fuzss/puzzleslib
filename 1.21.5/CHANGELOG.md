# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.5.12-1.21.5] - 2025-05-29
### Fixed
- Temporary workaround for `ClientLifecycleEvents$Started` firing too late for registering render layers on NeoForge

## [v21.5.11-1.21.5] - 2025-05-29
### Added
- Add `ItemTooltipRegistryV3`
### Changed
- Update custom configuration screens to better handle startup configs
- Update `RegistryManager::registerEntityDataSerializer` to use new Fabric Api hook
- Rework `CreativeModeTabHelper`, including support for painting variants
- Update `PlayerNetworkEvents` to use Fabric Api's new events in `ServerPlayerEvents`
- Update `AnvilEvents` for NeoForge 21.5.73+

## [v21.5.10-1.21.5] - 2025-05-01
### Added
- Add `MenuProviderWithData`

## [v21.5.9-1.21.5] - 2025-04-23
### Added
- Add `GuiLayersContext::setEventPhase`

## [v21.5.8-1.21.5] - 2025-04-23
### Added
- Add `GuiLayersContext`
- Add `SpawnerDataBuilder`
- Add `ItemTooltipRegistryV2`
- Add `ContainerMenuHelper::openMenu`
### Changed
 - Allow configs to be available during start-up
 - Make custom registries optional to handle mod presence mismatches between clients and servers more leniently on Fabric
### Fixed
- Only trigger `CommandOverrides` when commands are allowed
- Fix event phase converters not respecting the ordering of all parent event phases

## [v21.5.7-1.21.5] - 2025-04-14
### Changed
- Rework implementation for menu types with additional data sent to clients

## [v21.5.6-1.21.5] - 2025-04-14
### Added
- Add `SimpleContainerImpl`
- Add `RenderPipelinesContext`
- Add some overloads to `HSV`
### Fixed
- Fix `RenderPipelines` failing to register on NeoForge

## [v21.5.5-1.21.5] - 2025-04-10
### Added
- Add `ClientCommonHelper`
- Add `GuiHeightHelper::getMaxHeight` & `GuiHeightHelper::getMinHeight`
### Fixed
- Fix `ClassCastException` for `PlayLevelSoundEvents` on Fabric

## [v21.5.4-1.21.5] - 2025-04-03
### Added
- Add `HSV` utility class
- Add one more `RegistryManager::registerSpawnEggItem` overload
### Fixed
- Fix entity attributes failing to register on NeoForge

## [v21.5.3-1.21.5] - 2025-04-02
### Added
- Add `ItemTooltipRegistry`
- Add `EntityHelper::isPiglinCurrency`
- Add `AbstractLanguageProvider::mustHaveTranslationKey`
- Add a few more methods to `ClientComponentSplitter`
### Changed
- Slightly rework `AbstractAtlasProvider`
- Deprecate some method in `ComponentHelper`

## [v21.5.2-1.21.5] - 2025-04-02
### Added
- Add back `ModelLoadingHelper::missingModel`
- Add a few missing access wideners
### Removed
- Remove `BlockStateResolverContext$ResourceLoaderContext` as it can no longer be supported with current APIs
### Fixed
- Fix start-up crash on Fabric due to a faulty Mixin
- Temporarily fix `ClientLifecycleEvents$Started` not firing on NeoForge due to a bug in the mod loader

## [v21.5.1-1.21.5] - 2025-04-01
### Changed
- Overhaul internal development structure
### Fixed
- Fix client start-up crash on NeoForge caused by `Minecraft` being `null`

## [v21.5.0-1.21.5] - 2025-03-31
- Port to Minecraft 1.21.5
### Added
- Add `RegisterConfigurationTasksCallback`
- Add `ContentRegistrationHelper`for registering new skull block types
- Add `QuadUtils`
- Add `CodecExtras::LIST_TAG_CODEC`, `CodecExtras::setOf`, and `CodecExtras::decodeOnly`
### Changed
- Complete networking refactor, replacing `NetworkHandler` with `ModConstructor::onRegisterPayloadTypes`, and adding support for configuration phase messages
- Untie `PlayerSet` from only being able to handle sending packets
- Split `CommonAbstractions` into multiple classes such as `EntityHelper`, `EnchantingHelper`, and `ExplosionEventHelper`
- Split `ClientAbstractions` into multiple classes such as `GuiHeightHelper`, `ClientWoodTypeRegistry`, and `RenderTypeHelper`
- Merge `EntityAttributesCreateContext` & `EntityAttributesModifyContext` into `EntityAttributesContext`
- Refactor `ClientStartedCallback` into `ClientLifecycleEvents`
- Support directly registering a texture location in `SkullRenderersContext`
### Removed
- Remove `SpecialBlockModelTypesContext`
- Remove `JsonConfigFileUtil` & `JsonSerializationUtil`
- Remove `BlockInteractionsContext`, `CompostableBlocksContext`, `FlammableBlocksContext` & `FuelValuesContext`
- Remove `ReflectionHelper`
- Remove `RegisterFuelValuesCallback`
- Remove `RegistryHelper`
- Remove `MessageV2`
- Remove `ClientModConstructor::onRegisterAdditionalModels`
- Remove `ClientModConstructor::onRegisterCoreShaders`
- Remove `SetupMobGoalsCallback`
