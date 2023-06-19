# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v7.0.9-1.20] - 2023-06-19
### Added
- Added `MobEffectEvents`

## [v7.0.8-1.20] - 2023-06-17
### Added
- Added `PlayerEvents$BreakSpeed`
- Added `AbstractBuiltInDataProvider`
### Changed
- `AbstractDamageTypeProvider` is now deprecated for removal, use `AbstractBuiltInDataProvider` instead

## [v7.0.7-1.20] - 2023-06-16
### Fixed
- Multiple simple loot data providers should be allowed 

## [v7.0.6-1.20] - 2023-06-16
### Added
- Added data generator for particle descriptions
### Changed
- Streamlined existing data generators
- New helper methods in `RegistryManager` for biomes and particles

## [v7.0.5-1.20] - 2023-06-15
### Added
- Added `ItemEquipmentFactories` helper class
- Added a few more methods to `ToolTypeHelper`

## [v7.0.4-1.20] - 2023-06-14
### Added
- Added `ClientLevelEvents`
- Added `ToolTypeHelper`
- Added helper methods for dealing with enchantment tags

## [v7.0.3-1.20] - 2023-06-12
### Fixed
- Fixed arrow entity related crash on Fabric
- Fixed wrong parameter being passed for camera event on Forge

## [v7.0.2-1.20] - 2023-06-10
- Bump version

## [v7.0.1-1.20] - 2023-06-10
### Changed
- Minor improvements to helper methods for adding contents to creative tabs
- Made `ResourceKey`s in `CreativeModeTabs` accessible in common once again

## [v7.0.0-1.20] - 2023-06-09
- Ported to Minecraft 1.20

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
