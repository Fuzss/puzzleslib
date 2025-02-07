# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.4.5-1.21.4] - 2025-02-07
### Added
- Add `FuelValuesContext`, deprecating `RegisterFuelValuesCallback`
### Changed
- Update for NeoForge 21.4.84+

## [v21.4.4-1.21.4] - 2025-02-06
### Changed
- Allow `BlockStateResolverContext` to add `UnbakedModel` instances
### Fixed
- Fix `BlockStateResolverContext` not caching manually loaded `UnbakedModel` instances on NeoForge
- Fix `BlockStateResolverContext` constantly re-baking all previously located `UnbakedBlockStateModel` instances on NeoForge

## [v21.4.3-1.21.4] - 2025-02-05
### Added
- Add an alternative `BlockStateResolverContext::registerBlockStateResolver` implementation for preparing asynchronously loaded resources 
### Changed
- Rework methods in `ModelLoadingHelper` to return `CompletableFuture` instances
- Move selected item name together with gui height variables on Fabric just as NeoForge does
### Fixed
- Fix `ShieldBlockCallback` firing when the entity is not blocking on NeoForge
- Fix `ClientAbstractions::getGuiRightHeight` and `ClientAbstractions::getGuiLeftHeight` providing incorrect values for creative players on Fabric

## [v21.4.2-1.21.4] - 2025-02-04
### Added
- Add `BlockStateResolverContext`
- Add `ClientStartedCallback`
- Add `ModelLoadingHelper`
### Changed
- Delay running `AdditionalModelsContext` until `ModelLoadingPlugin` initialization on Fabric
### Fixed
- Fix `AbstractModelProvider` generating all vanilla item models on each run

## [v21.4.1-1.21.4] - 2025-02-03
### Changed
- Temporarily allow data generation to run by hijacking the client configuration while Architectury Loom remains broken

## [v21.4.0-1.21.4] - 2025-02-03
- Port to Minecraft 1.21.4
### Added
- Add `SpecialBlockModelTypesContext`
- Add `SpecialBlockModelRenderersContext`
- Add `GameRegistriesContext`
- Add `TransmuteRecipeHelper`
- Add `ExistingFilesHelper`
### Changed
- Replace `RenderNameTagEvents` with `RenderNameTagCallback`
- Replace `ExtractRenderStateCallback` with `ExtractRenderStateCallbackV2`
- Rework `ModelEvents` into `ModelLoadingEvents`, `BlockModelLoadingEvents`, and `ModelBakingCompletedCallback`
- Refactor `ColorProvidersContext` into `BlockColorsContext`
### Removed
- Remove `ContentRegistrationFlags`
- Remove `NeoForgeDataProviderContext`
- Remove `LootTableLoadEvents`
- Remove `RenderPlayerEvents`
- Remove `BuildCreativeModeTabContentsContext`
- Remove `CreativeModeTabContext` with `CreativeModeTabConfigurator`
- Remove `BuiltinModelItemRendererContext` with `BuiltinItemRenderer` and `ReloadingBuiltInItemRenderer`
- Remove `ItemModelPropertiesContext`
- Remove `ItemModelDisplayOverrides`
- Remove `AbstractRegistriesDatapackGenerator`
- Remove `AbstractModelProvider` with `ModItemModelProvider`
