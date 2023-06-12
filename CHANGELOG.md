# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v4.4.1-1.19.2] - 2023-06-12
### Fixed
- Fixed a class loading issue related to loading service provider interfaces

## [v4.4.0-1.19.2] - 2023-04-20
- Public release for all new features added since v4.3.12

## [v4.3.44-1.19.2] - 2023-02-27
### Fixed
- Actually implement new events accepting enqueued work haha

## [v4.3.43-1.19.2] - 2023-02-27
### Changed
- Allow for enqueuing work during mod construct and setup events

## [v4.3.42-1.19.2] - 2023-02-13
### Changed
- Added access to vanilla `BlockColors` and `ItemColors` to `ColorProvidersContext`

## [v4.3.41-1.19.2] - 2023-02-13
### Fixed
- Fixed vanilla item model overrides not being applied for items with different models depending on `TransformType`

## [v4.3.40-1.19.2] - 2023-02-07
### Added
- Added a method for registering flammable block properties in the common project
### Changed
- Some clean-up for common registration implementations on Forge

## [v4.3.39-1.19.2] - 2023-02-04
### Added
- Added a method for registering item and block colors in the common project
### Changed
- Refactored a few client-side registration methods to accept a varargs parameter
### Fixed
- Fixed an issue where networking messages would be registered in a different order on dedicated client/server

## [v4.3.38-1.19.2] - 2023-02-03
### Changed
- Improved type parameters in `LivingEntityRenderLayersContext`

## [v4.3.37-1.19.2] - 2023-02-01
### Fixed
- Fixed server configs failing to unload

## [v4.3.36-1.19.2] - 2023-02-01
### Fixed
- Fixed an issue when registering client and common configs on Fabric due to a forward reference

## [v4.3.35-1.19.2] - 2023-01-28
### Fixed
- Fixed potion recipes on Forge being broken

## [v4.3.33-1.19.2] - 2023-01-27
### Added
- Added utility methods to creative tab builder to automatically add all enchantments and potions from a mod
### Changed
- Applied transitive access widener to `AbstractSkeleton::getStepSound` to make the class extensible 

## [v4.3.32-1.19.2] - 2023-01-20
### Changed
- Improved handling of configs with default values (those are loaded when a config is accessed before it has been loaded)
- Built-in protection for `com.electronwill.nightconfig.core.io.ParsingException: Not enough data available`
- Puzzles Lib now explicitly requires the Minecraft version it's been compiled on (which is 1.19.2 currently)

## [v4.3.31-1.19.2] - 2023-01-18
### Changed
- Server mod configs are now stored in the global `.minecraft/config` directory instead of locally per world
- This effectively disables local per-world server configs, but helps a lot with user confusion and frustration

## [v4.3.30-1.19.2] - 2023-01-17
### Fixed
- Fixed backwards compatibility for a few changed method signatures

## [v4.3.29-1.19.2] - 2023-01-17
### Fixed
- Fix access level of methods in `BlockRenderTypesContext`

## [v4.3.28-1.19.2] - 2023-01-16
### Added
- Added a method for registering `RenderType`s for blocks and fluids in the common project
- Added a helper method for registering new recipe types
- Added `ContentRegistrationFlags` so a mod can request specific features that need to be registered to work to be enabled

## [v4.3.27-1.19.2] - 2023-01-13
### Added
- Added a utility method for getting a mod's display name in common

## [v4.3.26-1.19.2] - 2023-01-09
### Changed
- Use mod loader provided methods for registering brewing recipes instead of custom implementation

## [v4.3.25-1.19.2] - 2023-01-06
### Added
- Added `ModConstructor::onRegisterBiomeModifications` similar to Forge's old `BiomeLoadingEvent` from before Minecraft 1.19 for applying biome modifications in code in the common project rather than via data packs (like Forge does now)
- This includes a port of a big portion of Fabric API's biome api to the common workspace, so that Forge's current biome modification system can be implemented on top of it
### Changed
- Expanded `ReflectionHelperV2` to allow for passing target classes in as a raw type name to handle classes that may not be accessible or not present at runtime
- Networking classes related to the V2 implementation are no longer marked deprecated as they are still wildly used and the new system does not provide an advantage in most use cases

## [v4.3.24-1.19.2] - 2023-01-03
### Added
- Added `ItemModelOverrides` for registering custom item models for certain `ItemTransforms.TransformType`s (like trident and spyglass)
### Fixed
- Fixed custom `BlockEntityWithoutLevelRenderer`s not reloading when the resource listener does on Forge

## [v4.3.23-1.19.2] - 2023-01-01
### Changed
- Use `NonNullList`s when filling in creative mode tab items

## [v4.3.22-1.19.2] - 2022-12-31
### Fixed
- Fixed common mixins on Fabric

## [v4.3.21-1.19.2] - 2022-12-31
### Fixed
- Fixed byte buf serializer
- Fixed `fabric.mod.json` access widener entry

## [v4.3.20-1.19.2] - 2022-12-31
### Fixed
- Removed all invalid registry serializers

## [v4.3.19-1.19.2] - 2022-12-31
### Fixed
- Fixed duplicate message serializer

## [v4.3.18-1.19.2] - 2022-12-30
### Fixed
- Fixed access widener in `quilt.mod.json`

## [v4.3.17-1.19.2] - 2022-12-30
### Added
- Puzzles Lib now includes a few transitive access wideners in the Common and Fabric projects
- Added a new networking system based on Java's records, heavily inspired by the networking system found in [Owo Lib](https://www.curseforge.com/minecraft/mc-mods/owo-lib) by [Glisco](https://github.com/gliscowo)
- Added `PotionBrewingRegistry` for registering potions using `Ingredient` instead of `Item`, while also providing access in the Common project
- Added methods in `ClientModConstructor` for registering custom skull type renderers and entity shaders for spectator mode
- Added `SkullRenderersRegistry` on Fabric for registering models for custom skull types
- Added `EntitySpectatorShaderRegistry` on Fabric for registering entity shaders for spectator mode
- Added `AdditionalAddEntityData` interface for sending extra data to clients when an entity is added
### Changed
- `CommonAbstractions` now includes simplified methods for creating a new `CreativeModeTab`
- `RegistryReference` now includes two helper methods to check whether the reference is present or empty

## [v4.3.16-1.19.2] - 2022-12-15
### Changed
- Refined enabling search bar in custom creative mode tabs

## [v4.3.15-1.19.2] - 2022-12-15
### Fixed
- Fixed `java.lang.StackOverflowError` when rendering custom creative mode tab due to an oversight

## [v4.3.14-1.19.2] - 2022-12-15
### Added
- Added `CreativeModeTabBuilder`, replacing previous factory methods while allowing for setting many more options on a tab

## [v4.3.13-1.19.2] - 2022-12-15
### Added
- Added interface with default implementation for containers
- Added a helper method for checking if a `KeyMapping` is active
- Allow constructing `StairBlock` and `DamageSource` in common without an anonymous class
### Changed
- Moved `CreativeModeTab` factory to common packages, also add more options

## [v4.3.12-1.19.2] - 2022-10-19
### Added
- Added access to the `hoveredSlot` in the `CommonScreens` helper class
### Changed
- Moved all Service Provider Interfaces to their decentralized classes to prevent issues with early class loading when all SPIs are loaded simultaneously (this is not effective yet as the main `CoreServices` and `ClientCoreServices` currently remain for backwards compatibility)

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