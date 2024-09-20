# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.1.10-1.21.1] - 2024-09-20
### Changed
- Allow `ItemModelDisplayOverrides` to accept both `ResourceLocation` as well as `ModelResourceLocation`

## [v21.1.9-1.21.1] - 2024-09-18
### Added
- Add `TooltipBuilder::setTooltipLineProcessor`

## [v21.1.8-1.21.1] - 2024-09-18
### Changed
- Pass original `ClientTooltipPositioner` to tooltip positioner factory in `TooltipBuilder`

## [v21.1.7-1.21.1] - 2024-09-17
### Fixed
- Fix `LivingDropsCallback` failing to capture player drops on Fabric
- Fix some keybinds failing to trigger properly on Fabric

## [v21.1.6-1.21.1] - 2024-09-17
### Changed
- Overhaul `AbstractRegistriesDatapackGenerator` so that generated registry values can be used in other data providers

## [v21.1.5-1.21.1] - 2024-09-16
### Added
- Add new data attachment api
### Changed
- Revert codec support for capabilities, use attachments for that
- Rename `PlayerSet::notify` to `PlayerSet::broadcast`

## [v21.1.4-1.21.1] - 2024-09-14
### Changed
- Support codecs for capabilities
- Simplify `ModelLayerFactory` implementation

## [v21.1.3-1.21.1] - 2024-09-13
### Added
- Add `ScreenSkipper`
- Add `GsonCodecHelper`
- Add `TagFactory`

## [v21.1.2-1.21.1] - 2024-09-12
### Added
- Add `RegistryManager::registerTrimMaterial` and `RegistryManager::registerLootTable`

## [v21.1.1-1.21.1] - 2024-09-11
### Added
- Add `RegistryManager::registerDataComponentType`
### Changed
- Minor updates for `ConfigTranslationsManager`
- Hide some annoying toast messages in development environments
### Removed
- Remove `BlockEntityHelper`

## [v21.1.0-1.21.1] - 2024-09-10
- Port to Minecraft 1.21.1
