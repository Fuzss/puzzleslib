# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.10.4-1.21.10] - 2025-10-20

### Fixed

- Fix `java.lang.UnsupportedOperationException` when joining a world when a mod is trying to remove existing mob spawns
  on NeoForge

## [v21.10.3-1.21.10] - 2025-10-16

### Added

- Add `ParticleProvidersContext::registerParticleRenderType`

### Fixed

- Fix a crash that occurred when trying to set an attachment value on NeoForge

## [v21.10.2-1.21.10] - 2025-10-11

### Changed

- Update for NeoForge 21.10.7-beta
- Small refactors in `AbstractLanguageProvider`
- Attachment values are no longer set when the old value is equal to the new value

### Fixed

- Fix duplicate key categories
- Fix `GuiGraphicsHelper::blitNineSlicedSprite` using an invalid gui sprites atlas location

## [v21.10.1-1.21.10] - 2025-10-09

### Changed

- Update for NeoForge 21.10.3-beta

## [v21.10.0-1.21.10] - 2025-10-08

### Changed

- Update to Minecraft 1.21.10
