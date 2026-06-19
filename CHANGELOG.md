# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v26.2.1-mc26.2.x] - 2026-06-19

### Changed

- Add missing `BlockSetFamily::generateFor` method allowing for a custom name override

### Fixed

- Fix `RegistrySetBuilder` passed to a `PackGenerator` in `DataProviderHelper` not using the correct `PackOutput`

## [v26.2.0-mc26.2.x] - 2026-06-17

### Added

- Add `BlockSetVariant#BRICKS`, `BlockSetVariant#COBBLED`, `BlockSetVariant#TILES`, `BlockSetVariant#PILLAR`,
  `BlockSetVariant#LOG`, `BlockSetVariant#WOOD`, `BlockSetVariant#STRIPPED_LOG`, `BlockSetVariant#STRIPPED_WOOD`

### Changed

- Update to Minecraft 26.2.x
- Rework `RegistryManager::registerCreativeModeTab`
- Support base name overrides in `BlockSetFamily`
- `BlockSetFamily::wooden` now generates the new log and wood variants

### Fixed

- Fix pack metadata max format version
- Fix `ItemModelGenerationHelper::generateShield`

### Removed

- Remove `ClientInputEvents.MouseScroll` in favor of `HotbarScrollingCallback`
- Remove `TagsUpdatedCallback` in favor of `ServerResourcesLoadCallback` & `ClientTagsUpdatedCallback`
- Remove `ServerEntityLevelEvents` in favor of `ServerEntityEvents`
- Remove `EntityHelper::getMobSpawnReason`
- Remove `InteractionResultHelper`
- Remove `ClientModConstructor::onRegisterRenderBuffers`
- Remove `ClientWoodTypeRegistry`
