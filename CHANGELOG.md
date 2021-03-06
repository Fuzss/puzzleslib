# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v4.0.17-1.19] - 2022-07-20
### Changed
- Allow final category fields in annotated configs
### Fixed
- Fixed crash on dedicated servers due to faulty import

## [v4.0.15-1.19] - 2022-07-19
### Added
- Added access for screen buttons to `Screens` utility class
- Added a way for registering search trees

## [v4.0.14-1.19] - 2022-07-19
### Changed
- Made sure mods always use the correct mod event bus when registering game content
### Fixed
- Fixed bug in registry manager that would result in game content being registered for the wrong mod id

## [v4.0.13-1.19] - 2022-07-19
### Added
- Added a helper method for registering a material to the proper texture atlas
### Changed
- Moved method for baking configs into base interface
### Fixed
- Fixed a bug where `ConfigDataHolderV2` would always throw a `NullPointerException` during config load
- Fixed `ModEntityTypeBuilder` being created from supplier too early on Forge

## [v4.0.12-1.19] - 2022-07-18
### Added
- Added overhauled config system to allow for as many configs as desired regardless of type

## [v4.0.11-1.19] - 2022-07-17
### Added
- Added the mod loader type to the current mod loader environment context
### Fixed
- Fixed type arguments when registering placeholder registry references

## [v4.0.10-1.19] - 2022-07-17
### Changed
- Adjusted a few internal names for registering game content

## [v4.0.9-1.19] - 2022-07-17
### Added
- Added mod base interfaces for common and client with many new helper methods for registering content
- Added helper class for client factory methods
### Changed
- Converted `FuelManager` and `Screens` to no longer use a Service Provider Interface
### Fixed
- Fixed possible start-up crash on Forge due to methods in mixin classes not being properly obfuscated

## [v4.0.8-1.19] - 2022-07-13
- Fully compatible with Forge 41.0.98+ which is also now required
### Added
- Added a way for registering particle types
### Changed
- Renamed `Services` to `CoreServices`, old classes remain as overloads for now
### Fixed
- Fixed `RegistryManager` not returning the exact type it was provided
- Should no longer show as incompatible with servers that don't have Puzzles Lib

## [v4.0.7-1.19] - 2022-07-11
### Changed
- Small code clean-ups

## [v4.0.6-1.19] - 2022-07-11
### Added
- Added a few more utility methods for registering game content
### Fixed
- Fixed RegistryReference::placeholder always throwing an exception when used

## [v4.0.5-1.19] - 2022-07-09
### Fixed
- Attempt to fix common jar not being deobfuscated when applying from Maven in dependant projects

## [v4.0.4-1.19] - 2022-07-09
### Fixed
- Fixed common project depending on Quilt

## [v4.0.3-1.19] - 2022-07-09
### Added
- Added a utility class for registering client side content
### Fixed
- Fixed Modrinth upload tasks

## [v4.0.2-1.19] - 2022-07-07
### Changed
- Renamed internal base classes to follow mod loader specific names

## [v4.0.1-1.19] - 2022-07-07
### Fixed
- Fixed maven dependency for Forge version

## [v4.0.0-1.19] - 2022-07-07
- Ported to Minecraft 1.19
- Split into multi-loader project

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/