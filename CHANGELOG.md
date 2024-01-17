# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
