# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v26.2.0-mc26.2.x] - 2026-06-09

### Changed

- Update to Minecraft 26.2.x
- Rework `RegistryManager::registerCreativeModeTab`
- Support base name overrides in `BlockSetFamily`

### Fixed

- Fix pack metadata max format version
- Fix `ItemModelGenerationHelper::generateShield`

### Removed

- Remove `ClientInputEvents.MouseScroll` in favor of `HotbarScrollingCallback`
- Remove `TagsUpdatedCallback` in favor of `ServerResourcesLoadCallback` & `ClientTagsUpdatedCallback`
- Remove `ServerEntityLevelEvents` in favor of `ServerEntityEvents`
- Remove `EntityHelper::getMobSpawnReason`
- Remove `InteractionResultHelper`
