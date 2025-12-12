# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
