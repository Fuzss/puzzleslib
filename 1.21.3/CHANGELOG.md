# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
