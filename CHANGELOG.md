# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v6.0.11-1.19.4] - 2023-06-12
### Fixed
- Fixed arrow entity related crash on Fabric
- Fixed wrong parameter being passed for camera event on Forge

## [v6.0.10-1.19.4] - 2023-06-06
### Added
- Added many new events, including `ProjectileImpactCallback`, `LivingKnockBackCallback` and `ItemAttributeModifiersCallback`
- Added `GsonEnumHelper`
### Changed
- Implemented a few more `RenderGuiElementEvents`
### Fixed
- `ArrowLooseCallback` now also runs for crossbows on Fabric, just like Forge
- Fixed configs reporting as available too early
- Fixed an issue with service loaders when they were loaded by the wrong class loader

## [v6.0.9-1.19.4] - 2023-05-29
### Added
- Added `ClientPlayerEvents`

## [v6.0.8-1.19.4] - 2023-05-28
### Added
- Added support for legacy-like smithing recipes that do not require an upgrade template
- A few more helper methods when dealing with `DamageType`s

## [v6.0.7-1.19.4] - 2023-05-27
### Fixed
- Fixed a crash caused by screen init events on Forge

## [v6.0.6-1.19.4] - 2023-05-25
### Added
- Added `AbstractDamageTypeProvider` and related tag provider
- Added `DamageSourcesHelper`

## [v6.0.5-1.19.4] - 2023-05-24
### Added
- Added `ItemTossCallback`
### Changed
- Allow retrieving an actual `BlockColor` / `ItemColor` from `ColorProviderContext`
- Some more internal clean-ups
### Fixed
- Prevent `CustomizeChatPanelCallback` from interfering with more than the chat panel
- Fixed faulty `BowItem` mixin on Fabric

## [v6.0.4-1.19.4] - 2023-05-22
### Added
- Added a few new rendering related client events
- Added `ScreenElementPositioner` helper class
- Added a bunch of new `Level` related events
### Changed
- Refactored a lot of mod construction contexts
### Fixed
- Fixed `NullPointerException` when firing `ComputeFovModifierCallback` on Fabric
- Fixed `ComputeFovModifierCallback` dividing by zero when fov effects are disabled
- Fixed `Screen::init` events not providing a view of `AbstractWidget`s on Forge

## [v6.0.3-1.19.4] - 2023-05-20
### Fixed
- Fixed `ClassCastException` for `RenderGuiElementEvents`

## [v6.0.2-1.19.4] - 2023-05-20
### Changed
- Implemented `RenderGuiElementEvents` for item name
- Moved `SpawnDataMob` implementation to Fabric, the native `Mob::getSpawnType` method is now used on Forge
- Allow more context for `RenderGuiElementEvents` to ease implementations on different mod loaders 
### Fixed
- Fixed `ClassCastException` for after events in `ScreenMouseEvents`

## [v6.0.1-1.19.4] - 2023-05-19
### Added
- Added events for when the player is being cloned and for after respawning
### Changed
- Some internal clean-up for capabilities and networking

## [v6.0.0-1.19.4] - 2023-05-18
- Ported to Minecraft 1.19.4
### Changed
- Internal changes to capability implementation on Fabric to make capabilities other than for entities actually usable and not result in class loading errors

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
