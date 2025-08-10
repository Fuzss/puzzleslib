# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.8.5-1.21.8] - 2025-08-10

### Fixed

- Fix entity interaction events not functioning properly on Fabric

## [v21.8.4-1.21.8] - 2025-08-04

### Added

- Add `AbstractLootProvider.Blocks::createHeadDrop`
- Add `CommonHelper::getRegistryAccess`

### Changed

- Tag contents constructed via `AbstractTagProvider` are now sorted to ensure consistent results for dynamically added
  data pack registry entries

## [v21.8.3-1.21.8] - 2025-07-31

### Added

- Add `PackRepositorySourcesContext::registerBuiltInPack`
- Support built-in packs in `DataProviderHelper`
- Add `TransformingForwardingList`

### Changed

- `pack.mcmeta` is now always created during data generation for every mod

## [v21.8.2-1.21.8] - 2025-07-27

### Fixed

- Fix `Config has already been registered` exception

## [v21.8.1-1.21.8] - 2025-07-27

### Added

- Add `AnchorPoint`
- Add `CommonHelper::getBlockableEventLoop`

### Changed

- Overhaul `ComponentHelper` implementation to properly support legacy formatting codes passed as part of a string

### Fixed

- Fix some quirks with player interaction events on Fabric

## [v21.8.0-1.21.8] - 2025-07-18

- Update to Minecraft 1.21.8
