# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v26.2.2-mc26.2.x] - 2026-08-04

### Added

- Add `ScreenEvents.AfterForeground`
- Add `ItemHelper::shouldDisplayAllTooltipInformation`

### Changed

- Properly use Fabric's new character type events
- Simplify `ItemComponentsContextFabricImpl` implementation
- Migrate implementation in `BuiltInBlockModelsContextFabricImpl` to Fabric's new native callback
- Use accumulated scroll values provided by NeoForge's `MouseScrollingEvent` instead of capturing them manually
- Avoid creating a custom `ModelBakery` for `BlockStateResolverContext` to improve compatibility with other mods on
  NeoForge

### Fixed

- Fix crashes on start-up on newer versions of NeoForge

## [v26.2.1-mc26.2.x] - 2026-07-13

### Changed

- Add missing `BlockSetFamily::generateFor` method allowing for a custom name override
- `AbstractModelProvider::createVariantWoodBlockProviders` now includes log and wood blocks
- Recipe serializers registered via `ContentRegistrationHelper::registerTransmuteRecipeSerializers` are now
  automatically synced to clients on Fabric to allow for recipe viewer mods to pick them up properly

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
