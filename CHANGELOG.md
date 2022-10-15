# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v4.3.11-1.19.2] - 2022-10-15
### Fixed
- Temporary fix for start-up crash due to a class being loaded before all mixins could apply when Stylish Effects is installed on Quilt

## [v4.3.10-1.19.2] - 2022-10-12
### Added
- Added new `ReflectionHelperV2` class, the old one is now deprecated

## [v4.3.9-1.19.2] - 2022-10-04
### Changed
- Limited error messages from config values being used when a config is not yet available to showing up just once to avoid log spam 

## [v4.3.8-1.19.2] - 2022-09-26
### Changed
- Improve handling of registry manager placeholder entries

## [v4.3.7-1.19.2] - 2022-09-24
### Fixed
- Fixed existing `NetworkHandler`'s being recreated 

## [v4.3.6-1.19.2] - 2022-09-23
### Added
- Added an event for registering key binds

## [v4.3.5-1.19.2] - 2022-09-14
### Added
- Added a way for opening menus with additional data

## [v4.3.4-1.19.2] - 2022-09-14
### Changed
- Added a few small utility methods to `ConfigDataSet`

## [v4.3.3-1.19.2] - 2022-09-14
### Changed
- Overhauled internal handling of config options for game registry data (e.g. a list of items or blocks) to allow for tag support and greater flexibility when defining these options
### Fixed
- Fixed an issue where client-only mods would sometimes not properly work on servers

## [v4.3.2-1.19.2] - 2022-09-13
### Fixed
- Fixed baked model event firing too late, resulting in any proposed modifications being lost

## [v4.3.1-1.19.2] - 2022-09-01
### Fixed
- Set dependency on Minecraft 1.19.2+ due to features introduced in Forge 1.19.2-43.1.0 being used

## [v4.3.0-1.19.2] - 2022-09-01
- Public release for all additions and changes made since v4.2.4
- This breaks a small number of mods, they'll quickly be updated

## [v4.2.17-1.19.2] - 2022-08-31
### Added
- Added a way for deferring content registration on Fabric to better support when registration entries depending on another (e.g. blocks and the corresponding block entity) are added separately in both the common and the Fabric subproject

## [v4.2.16-1.19.2] - 2022-08-30
### Fixed
- Fixed context for registering living entity layers not containing all required instances

## [v4.2.15-1.19.2] - 2022-08-30
### Added
- Allow for registering living entity layers in common
### Changed
- Added a bunch of null safety checks for methods involved with mod construction

## [v4.2.14-1.19.2] - 2022-08-30
### Added
- Allow for registering custom client resource managers in common

## [v4.2.13-1.19.2] - 2022-08-29
### Added
- Allow for registering listeners that run after baked models have been reloaded
- This includes a helper method for baking a model in the common project
### Changed
- Mod service classes should no longer extend `CoreServices`, method for loading a service is now accessible from outside
### Removed
- Removed `ModelEvents` class on Fabric, the same can be achieved by registering a `ReloadListener` for baked models

## [v4.2.12-1.19.2] - 2022-08-28
### Added
- Allow for registering item decorators (like stack count) in common project, includes api implementation on Fabric

## [v4.2.11-1.19.2] - 2022-08-27
### Added
- Allow for registering custom `BlockEntityWithoutLevelRenderer`'s in the common project

## [v4.2.10-1.19.2] - 2022-08-27
### Fixed
- Fixed setting a player sync strategy for a capability not being accessible from the common project

## [v4.2.9-1.19.2] - 2022-08-27
### Fixed
- Fixed retrieving capability id falsely requiring the capability to be present on Forge

## [v4.2.8-1.19.2] - 2022-08-27
### Added
- Added support for automatically syncing player capabilities
### Changed
- Capability player respawn strategies are now handled by the capability key
### Fixed
- Fixed client player instance sometimes being `null` for received network messages on Forge

## [v4.2.7-1.19.2] - 2022-08-26
### Fixed
- Fix capability data failing to save on Forge

## [v4.2.6-1.19.2] - 2022-08-26
### Added
- Added utility method for registering item model predicates
- Added support for sending messages to players tracking an entity in `NetworkHandler`
### Changed
- Moved a bunch of duplicate code in `NetworkHandler` to common project

## [v4.2.5-1.19.2] - 2022-08-26
### Added
- Added utility method for creating creative mode tabs

## [v4.2.4-1.19.2] - 2022-08-26
### Changed
- Cardinal Components is once again included in the mod jar, this is done to be able to have a single component initializer for all my mods, while also being safe from not all required modules of Cardinal Components being present at runtime
### Fixed
- Fixed start-up crash for mods that use this library and Cardinal Components

## [v4.2.3-1.19.2] - 2022-08-23
### Added
- Added a few utility methods for modifying/replacing loot tables

## [v4.2.2-1.19.2] - 2022-08-22
### Fixed
- Temporarily fixed crash with other mods including only a few modules of Cardinal Components

## [v4.2.1-1.19.2] - 2022-08-21
### Changed
- Minor networking `Message` cleanup

## [v4.2.0-1.19.2] - 2022-08-20
- Compiled for Minecraft 1.19.2
### Changed
- Rewrote `CapabilityController` to reduce duplicate code on different mod loaders required for capability registration
- Renamed `PacketHandler` to `MessageHandler`
- Updated for changes in Fabric Api 0.60.0+1.19.2

## [v4.1.8-1.19.1] - 2022-08-16
### Added
- Added utility method for command registration

## [v4.1.6-1.19.1] - 2022-08-06
### Changed
- Made network manager check messages are being sent to the correct side on Fabric

## [v4.1.5-1.19.1] - 2022-08-06
### Fixed
- Fix type constraints when registering network messages

## [v4.1.4-1.19.1] - 2022-08-01
### Fixed
- Fixed client configs causing dedicated servers to fail on start-up

## [v4.1.3-1.19.1] - 2022-08-01
### Added
- Added a few utility methods for handling block models

## [v4.1.2-1.19.1] - 2022-07-31
### Fixed
- Fixed log spam when loading configs

## [v4.1.1-1.19.1] - 2022-07-30
### Fixed
- Correctly handle config unloading

## [v4.1.0-1.19.1] - 2022-07-30
- Compiled for Minecraft 1.19.1
### Changed
- Cleaned up networking classes
- Made config annotations more powerful, removing the need to set category names and comments on config objects
### Removed
- Remove deprecated methods and classes

## [v4.0.18-1.19] - 2022-07-26
### Added
- Added a utility class for creating and registering new game rules
### Changed
- Switched `CommonScreens` class back to using an SPI
- Renamed config related helper methods 

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