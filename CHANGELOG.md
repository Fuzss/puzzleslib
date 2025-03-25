# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.5.0-1.21.5] - 2025-03-25
- Port to Minecraft 1.21.5
### Added
- Add `ContentRegistrationHelper`for registering new skull block types
### Changed
- Merge `EntityAttributesCreateContext` & `EntityAttributesModifyContext` into `EntityAttributesContext`
- Refactor `ClientStartedCallback` into `ClientLifecycleEvents`
- Support directly registering a texture location in `SkullRenderersContext`
### Removed
- Remove `SpecialBlockModelTypesContext`
- Remove `JsonConfigFileUtil` & `JsonSerializationUtil`
- Remove `BlockInteractionsContext`, `CompostableBlocksContext`, `FlammableBlocksContext` & `FuelValuesContext`
- Remove `ReflectionHelper`
- Remove `RegisterFuelValuesCallback`
- Remove `RegistryHelper`
