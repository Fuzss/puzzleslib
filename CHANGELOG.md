# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.3.6-1.21.3] - 2024-11-24
### Changed
- Implement missing methods for `AbstractRecipeProvider`
### Fixed
- Fix clearing all effects crashing on Fabric

## [v21.3.5-1.21.3] - 2024-11-23
### Changed
- Implement `ExtractRenderStateCallback` using a dedicated mixin

## [v21.3.4-1.21.3] - 2024-11-23
### Added
- Add `ExtractRenderStateCallback`
- Add `RenderPropertyKey`

## [v21.3.3-1.21.3] - 2024-11-19
### Added
- Add `NativeImageHelper`
### Fixed
- Fix `LivingChangeTargetCallback` implementation on Fabric

## [v21.3.2-1.21.3] - 2024-11-19
### Fixed
- Update `GuiGraphicsHelper` to account for removed `blitOffset` parameter in `GuiGraphics`
- Fix `ClientAbstractions::registerConfigScreenFactory` throwing an exception on NeoForge

## [v21.3.1-1.21.3] - 2024-11-18
### Added
- Add `InteractionResultHelper`
### Fixed
- Fix `PuzzlesLib::isDevelopmentEnvironmentWithoutDataGeneration` being inverted

## [v21.3.0-1.21.3] - 2024-11-15
- Port to Minecraft 1.21.3
### Added
- Add `ClientAbstractions::registerConfigScreenFactory`
### Changed
- Rework `RenderNameTagCallback` into `RenderNameTagEvents` for adapting Minecraft changes
- Replace `FuelBurnTimesContext` with `RegisterFuelValuesCallback`
- Rename `CopyComponents` recipes to `Transmute` recipes
- Replace `ChatMessageReceivedEvents` with a simplified `ChatMessageReceivedCallback`
### Removed
- Remove `MinecartTypeRegistry`
- Remove `RenderGuiCallback` in favor of `RenderGuiEvents`
- Remove `GenericExplosionHelper`
