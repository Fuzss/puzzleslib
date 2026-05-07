# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v26.1.7-mc26.1.x] - 2026-05-07

### Fixed

- Fix breaking the Do a Barrel Roll mod

## [v26.1.6-mc26.1.x] - 2026-05-06

### Added

- Add `SimpleHumanoidArmorLayer` and `SimpleItemInHandLayer`

### Fixed

- Fix `EventResultHolder` not accepting a `null` value

## [v26.1.5-mc26.1.x] - 2026-05-06

### Added

- Add `ServerEntityEvents`, deprecating `ServerEntityLevelEvents`
- Add `HotbarScrollingCallback`, deprecating `ClientInputEvents::MouseScroll`
- Add an additional `ItemComponentsContext::registerItemComponentsPatch` overload

### Fixed

- Fix crash with the Do a Barrel Roll mod

## [v26.1.4-mc26.1.x] - 2026-04-30

### Added

- Add `SingleChestRenderer::createXmasChest`
- Add `ItemComponentsContext$InitializerV2`

### Changed

- Update `ServerEntityLevelEvents$Load` to use the native Fabric event

## [v26.1.3-mc26.1.x] - 2026-04-29

### Fixed

- Properly support data components in `LazyHolder`

## [v26.1.2-mc26.1.x] - 2026-04-27

### Changed

- Refactor `SingleChestRenderer`
- Skip world upgrade screens

### Fixed

- Fix texture return types in `ModelLocationHelper`
- Fix recompilation failing for the common subproject due to a missing access transformer entry

## [v26.1.1-mc26.1.x] - 2026-04-26

### Fixed

- Fix the Mixin config plugin package for common

## [v26.1.0-mc26.1.x] - 2026-04-24

### Changed

- Update to Minecraft 26.1.x
- Refactor `SpecialBlockModelRenderersContext` as `BuiltInBlockModelsContext`
- Replace with `ItemComponentsContext` & `ModConstructor::onRegisterItemComponentPatches`
- `CustomTransmuteRecipes` no longer store the `RecipeSerializer`, instead they use the ResourceKey for dynamically
  retrieving the serializer instance
- Rename `ExtractRenderStateCallback` to `ExtractEntityRenderStateCallback`
- Rename `DrawItemStackOverlayCallback` to `ExtractItemStackDecorationsCallback`

### Removed

- Remove `ClientModConstructor::onRegisterBlockRenderTypes` & `ClientModConstructor::onRegisterFluidRenderTypes`
- Remove `BlockColorsContext::getBlockColor`
- Remove `SpritelessImageButton`
- Remove `MutableBakedQuad::hasAmbientOcclusion`
- Remove `ChunkSectionLayer` handling in `ClientBlockSetFamily`
- Remove `VillagerTradesContext`
- Remove plain `TickingBlockEntity::clientTick` and `TickingBlockEntity::serverTick` methods
- Remove `AbstractLanguageProvider$BlockFamilyBuilder`
- Remove `PlayerInteractEvents$UseEntityAt` in favor of `PlayerInteractEvents$UseEntity`
