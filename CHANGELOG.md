# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.5.4-1.21.5] - 2025-04-03
### Add
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
