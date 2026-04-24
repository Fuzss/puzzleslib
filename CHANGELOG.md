# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
