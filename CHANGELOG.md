# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
