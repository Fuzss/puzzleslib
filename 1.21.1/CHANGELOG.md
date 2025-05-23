# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.1.36-1.21.1] - 2025-05-04
### Changed
- Allow `DataProviderHelper` to support `RegistrySetBuilder`

## [v21.1.35-1.21.1] - 2025-05-04
### Removed
- Remove unused breathing and drowning event implementations on Fabric to improve compatibility with Lunar Client

## [v21.1.34-1.21.1] - 2025-04-25
### Changed
- Improve compatibility with other mods injecting into the `Gui` class on Fabric

## [v21.1.33-1.21.1] - 2025-03-16
### Fixed
- Fix `ModelPart$Vertex` & `ModelPart$Polygon` being inaccessible on NeoForge

## [v21.1.32-1.21.1] - 2025-03-14
### Changed
- Add a bunch of safety checks for casting in event invokers

## [v21.1.31-1.21.1] - 2025-03-13
### Added
- Add `ModConstructor::onRegisterGameplayContent`
- Add `ModelLocationHelper` & `ModelTemplateHelper`
- Add `ResourceKeyHelper`
- Add `AbstractDatapackRegistriesProvider`

## [v21.1.30-1.21.1] - 2025-03-11
### Fixed
- Fix a rare start-up crash when registering generated config translations on NeoForge

## [v21.1.29-1.21.1] - 2025-03-09
### Fixed
- Fix CommonAbstractions::hasChannel crashing for invalid players on NeoForge

## [v21.1.28-1.21.1] - 2025-02-25
### Added
- Backport new block, item and creative mode tab registration methods to `RegistryManager`
- Backport `CompostableBlocksContext`
### Changed
- Update `FabricEventFactory` to handle faulty return values more leniently

## [v21.1.27-1.21.1] - 2025-01-17
### Fixed
- Fix network packets executing with one tick delay on Fabric

## [v21.1.26-1.21.1] - 2025-01-16
### Changed
- Allow setting empty lines for `TooltipBuilder` to prevent the tooltip from drawing
### Fixed
- Fix unable to set a new widget tooltip using vanilla methods when `TooltipBuilder` has been used for the widget

## [v21.1.25-1.21.1] - 2025-01-10
### Fixed
- Avoid unnecessarily high memory usage from holding on to top level model locations after model events have completed
- Wrap `BlockStateModelLoader` in a `WeakReference` to avoid holding on to it when it is no longer needed

## [v21.1.24-1.21.1] - 2024-12-22
### Changed
- Adjust `RangedSliderButton` implementation to no longer require an access widener to help compatibility with the [Cloth Config](https://modrinth.com/mod/cloth-config) mod

## [v21.1.23-1.21.1] - 2024-10-24
### Changed
- Support using `AbstractTagAppender` as string list builder

## [v21.1.22-1.21.1] - 2024-10-22
### Added
- Add `ChangeEntitySizeCallback`
- Add `RegistryManager::registerAttribute`
### Changed
- Expand `LivingEntityRenderLayersContext` with some additional methods

## [v21.1.21-1.21.1] - 2024-10-10
### Changed
- Allow `PlayerSet` to handle a provided client level more leniently

## [v21.1.20-1.21.1] - 2024-10-10
### Added
- Add `CommonAbstractions::getPartEntityParent`
### Fixed
- Fix removed data attachments failing to synchronize to clients

## [v21.1.19-1.21.1] - 2024-10-05
### Changed
- Replace `ClientParticleTypes` with `ClientParticleHelper`

## [v21.1.18-1.21.1] - 2024-10-04
### Changed
- Add more helper methods to `GuiGraphicsHelper`
### Fixed
- Fix `DynamicPackResources` failing to generate some resources

## [v21.1.17-1.21.1] - 2024-10-01
### Added
- Add `ClientAbstractions::hasChannel` & `CommonAbstractions::hasChannel`
### Changed
- Network messages are no longer sent to clients & servers that do not understand them

## [v21.1.16-1.21.1] - 2024-09-30
### Changed
- `RenderGuiLayerEvents` no longer fire when the hud is hidden via `F1`

## [v21.1.15-1.21.1] - 2024-09-30
### Changed
- Slight `RenderGuiEvents` adjustments
### Fixed
- Fix some gui layers from other mods failing to render

## [v21.1.14-1.21.1] - 2024-09-29
### Added
- Add `RenderGuiEvents`, replacing `RenderGuiCallback`

## [v21.1.13-1.21.1] - 2024-09-29
### Changed
- Overhaul `RenderGuiLayerEvents` implementation on Fabric

## [v21.1.12-1.21.1] - 2024-09-29
### Added
- Add `NeoForgeCapabilityHelper::registerItemContainer`
- Add `GuiGraphicsHelper::blitTiledSprite`
- Add a bunch of helper methods to `ClientAbstractions` for handling `Gui#leftHeight` &amp; `Gui#rightHeight`
### Changed
- Minor `ConfigTranslationsManager` improvements
### Removed
- Remove `ModLoaderEnvironment::getObjectShareAccess`

## [v21.1.11-1.21.1] - 2024-09-25
### Added
- Add `CodecExtras`
- Add some helper methods to `ItemModelDisplayOverrides`
- Add enum helpers to `ExtraStreamCodecs`
- Add some helper methods to `CreativeModeTabConfigurator`
- Add helper methods to `ShapesHelper` for rotating shapes horizontally
### Changed
- Allow for compatibility with Minecraft 1.21 on Fabric if enabled via [dependency overrides](https://fabricmc.net/wiki/tutorial:dependency_overrides)
- Allow `RegistryManager::registerBlockItem` to take an `Item$Properties` supplier
### Fixed
- Fix `RenderHighlightCallback` crashing on NeoForge

## [v21.1.10-1.21.1] - 2024-09-20
### Changed
- Allow `ItemModelDisplayOverrides` to accept both `ResourceLocation` as well as `ModelResourceLocation`

## [v21.1.9-1.21.1] - 2024-09-18
### Added
- Add `TooltipBuilder::setTooltipLineProcessor`

## [v21.1.8-1.21.1] - 2024-09-18
### Changed
- Pass original `ClientTooltipPositioner` to tooltip positioner factory in `TooltipBuilder`

## [v21.1.7-1.21.1] - 2024-09-17
### Fixed
- Fix `LivingDropsCallback` failing to capture player drops on Fabric
- Fix some keybinds failing to trigger properly on Fabric

## [v21.1.6-1.21.1] - 2024-09-17
### Changed
- Overhaul `AbstractRegistriesDatapackGenerator` so that generated registry values can be used in other data providers

## [v21.1.5-1.21.1] - 2024-09-16
### Added
- Add new data attachment api
### Changed
- Revert codec support for capabilities, use attachments for that
- Rename `PlayerSet::notify` to `PlayerSet::broadcast`

## [v21.1.4-1.21.1] - 2024-09-14
### Changed
- Support codecs for capabilities
- Simplify `ModelLayerFactory` implementation

## [v21.1.3-1.21.1] - 2024-09-13
### Added
- Add `ScreenSkipper`
- Add `GsonCodecHelper`
- Add `TagFactory`

## [v21.1.2-1.21.1] - 2024-09-12
### Added
- Add `RegistryManager::registerTrimMaterial` and `RegistryManager::registerLootTable`

## [v21.1.1-1.21.1] - 2024-09-11
### Added
- Add `RegistryManager::registerDataComponentType`
### Changed
- Minor updates for `ConfigTranslationsManager`
- Hide some annoying toast messages in development environments
### Removed
- Remove `BlockEntityHelper`

## [v21.1.0-1.21.1] - 2024-09-10
- Port to Minecraft 1.21.1
