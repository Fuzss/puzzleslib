# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v20.4.3-1.20.4] - 2024-01-20
### Added
- Add `SpritelessImageButton`
- Add `NbtSerializable`
- Add `RegistryHelper::getBuiltInRegistryHolder`

## [v20.4.2-1.20.4] - 2024-01-20
### Added
- Add `MenuScreensContext` for registering menu screen factories
### Changed
- Allow `EnumProvider` to accept a custom namespace

## [v20.4.1-1.20.4] - 2024-01-19
### Added
- Add `KeyedValueProvider` for serializing values not from a registry in `ConfigDataSet`
### Changed
- Revert `RegistryManager` returning `Holder` instead of `Holder.Reference`
- Migrate `init.v2` to `init.v3`
- Update some javadoc
- Turn some methods public in data providers
### Fixed
- Fix unable to instantiate `AbstractRecipeProvider` on NeoForge due to an additional constructor parameter being patched in
### Removed
- Remove `Proxy::getKeyMappingComponent`

## [v20.4.0-1.20.4] - 2024-01-17
- Ported to Minecraft 1.20.4
- Ported to NeoForge
- Support Mixin Extras
### Changed
- Overhauled capability api as `v3`, `v2` has been removed
- Renamed `RegistryManagerV3` to `RegistryManager`, now uses `Holder` instead of `Holder.Reference`
- Restructured event implementations, also delayed event loading to avoid being blamed for class loading errors
- Data generation has been exclusively moved to NeoForge
- Minor overhauls to `NetworkHandlerV2` regarding `Message` registration
### Removed
- Removed `RegistryManagerV2`
- Removed bundled PuzzlesAccessApi in favor of Loom's access wideners
