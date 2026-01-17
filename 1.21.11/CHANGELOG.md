# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.11.7-1.21.11] - 2026-01-17

### Added

- Add additional helper methods `ComponentHelper::getAsComponent` and `ComponentHelper::getAsString`

### Changed

- Move `StyleCombiningCharSink` to api package
- Adjust the injection point for `GatherEffectScreenTooltipCallback` on Fabric

## [v21.11.6-1.21.11] - 2026-01-09

### Fixed

- Fix `GatherPotentialSpawnsCallback` not firing for nether fortress spawns on Fabric

## [v21.11.5-1.21.11] - 2026-01-03

### Fixed

- Fix `MobEffectEvents$Remove` running when the `MobEffectInstance` is `null`
- Fix `MobEffectEvents$Expire` not allowing for returning an `EventResult`

## [v21.11.4-1.21.11] - 2025-12-20

### Added

- Add `GameRuleUpdatedCallback`
- Add back `GatherEffectScreenTooltipCallback`

## [v21.11.3-1.21.11] - 2025-12-17

### Added

- Add `PackResourcesHelper::isPackHidden` and `PackResourcesHelper::setPackHidden`

### Changed

- Enable all model-related events again on Fabric

## [v21.11.2-1.21.11] - 2025-12-14

### Added

- Add `MutableBakedQuad`

## [v21.11.1-1.21.11] - 2025-12-13

### Fixed

- Fix `ExtractRenderStateCallback` not running on Fabric

## [v21.11.0-1.21.11] - 2025-12-13

### Added

- Add `DataPackReloadListenersContext`

### Changed

- Update to Minecraft 1.21.11
- Refactor `SubmitBlockOutlineCallback` into `ExtractBlockOutlineCallback`
- Expand context for `RenderBlockOverlayCallback`
- Unify `AbstractTagAppender`, while also removing non-optional string-based registration methods
- Provide `InteractionHand` parameter in `UseItemEvents`
- Rename `PlayerNetworkEvents` to `JOIN` and `LEAVE`
- Rename `RenderNameTagCallback` to `SubmitNameTagCallback`
- Rename `RenderLivingEvents` to `SubmitLivingEntityEvents`

### Removed

- Remove `ResourceLocationHelper`
- Remove the partial tick parameter from post-extraction events and methods
- Remove `ContainerMenuHelper::addInventorySlots`
- Remove `RegistryManager::registerLegacySpawnEggItem`
- Replace `SearchTreeType` with vanilla's `ContextKey`
- Remove `AddDataPackReloadListenersCallback` & `AddResourcePackReloadListenersCallback`
- Remove `GatherEffectScreenTooltipCallback` as the current implementation is not flexible enough for proper usage
