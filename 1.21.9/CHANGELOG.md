# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.9.1-1.21.9] - 2025-10-02

### Fixed

- Fix compatibility with the latest NeoForge version

## [v21.9.0-1.21.9] - 2025-09-27

### Added

- Add `ResourcePackReloadListenersContext`
- Add `ScreenHelper::isDoubleClick`

### Changed

- Update to Minecraft 1.21.9
- Update mouse and keyboard events to implement vanilla's new `MouseButtonEvent` and `KeyEvent`
- `GameplayContentContext::registerStrippable` not longer requires blocks to have the `AXIS` property
- `AddDataPackReloadListenersCallback` now provides the full `ReloadableServerResources`
- Split `ContainerScreenEvents` into `ScreenEvents$AfterBackground` and `RenderContainerScreenContentsCallback`
- Rework `SingleChestRenderer` to also provide a dedicated render state implementation

### Removed

- Remove `NamedReloadListener`
- Remove specialised Fabric registries, merging them directly with `ModConstructor` contexts
- Remove `ModelBakingCompleteCallback`
- Remove `RenderPropertyKey` in favour of vanilla's `ContextKey`
- Remove `CommonHelper::onExplosionStart`
- Remove `EnumProvider`
- Remove `GatherDebugInformationEvents`
- Remove `RenderHighlightCallback` until mod loaders provide a replacement
- Remove `RenderLevelCallback` until mod loaders provide a replacement
- Remove `DynamicallyCopiedPackResources`
