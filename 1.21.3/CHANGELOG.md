# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.3.24-1.21.3] - 2025-01-29
### Fixed
- Fix `CompostableBlocksContext` running too early to use item objects directly again

## [v21.3.23-1.21.3] - 2025-01-29
### Fixed
- Fix `CompostableBlocksContext` running too early to use item objects directly

## [v21.3.22-1.21.3] - 2025-01-29
### Added
- Add `CompostableBlocksContext`

## [v21.3.21-1.21.3] - 2025-01-29
### Added
- Add `DataPackRegistriesContext`
- Add `AbstractRecipeProvider::foodCooking`
- Add `BlockFamilyBuilder::hangingSign`

## [v21.3.20-1.21.3] - 2025-01-28
### Added
- Add `LootTableLoadCallback`, deprecating `LootTableLoadEvents`
- Add `TransformingRecipeOutput`

## [v21.3.19-1.21.3] - 2025-01-26
### Added
- Add `ClientAbstractions::registerWoodType`
- Add one more `RegistryManager::registerBlockItem` overload
### Removed
- Remove `BlockFamilyBuilder::wallSign`

## [v21.3.18-1.21.3] - 2025-01-24
### Added
- Add `QuickMoveRuleSet`
- Add `ArmorMaterialBuilder`
- Add `PickProjectileCallback`
- Add `EnderPearlTeleportCallback`
- Add `BuildCreativeModeTabContentsCallback`
- Add `AbstractLanguageProvider$BlockFamilyBuilder`
- Add `RegistryManager::registerCreativeModeTab`
- Add `RegistryManager::registerRecipeBookCategory`
- Add internal `ModEnchantCommand`
- Add a few new transitive access wideners
### Fixed
- Avoid unnecessarily high memory usage from holding on to top level model locations after model events have completed
- Fix setting empty lines for `TooltipBuilder` throwing an exception under some circumstances
- Fix unable to set a new widget tooltip using vanilla methods when `TooltipBuilder` has been used for the widget
- Fix network packets executing with one tick delay on Fabric
### Removed
- Remove `LivingBreathEvents` as the implementation is too invasive to maintain on Fabric

## [v21.3.17-1.21.3] - 2025-01-07
### Added
- Add `AbstractEquipmentModelProvider`
- Add `ClientAbstractions::isEffectVisibleInGui` & `ClientAbstractions::isEffectVisibleInInventory`
- Add missing methods to `InteractionResultHelper`
- Add a few new transitive access wideners
- Add one more `RegistryManager::registerItem` overload
### Changed
- Support instruments in `AbstractRegistriesDatapackGenerator`
- Overhaul `AbstractRegistriesDatapackGenerator` into `AbstractDatapackRegistriesProvider`
### Fixed
- Fix `GatherEffectScreenTooltipCallback` on Fabric

## [v21.3.16-1.21.3] - 2024-12-27
### Added
- Add `SingleChestRenderer`
- Add `ClientAbstractions::getPartialTick` for extracting partial tick time from `EntityRenderState`
- Add `ModelLocationHelper` and `ModelTemplateHelper` from preparing to move some helper methods out of `AbstractModelProvider`
### Changed
- Pass potion name parameter in `RegistryManager::registerPotion`
### Fixed
- Fix cancelling `InventoryMobEffectsCallback` leading to an exception on Fabric

## [v21.3.15-1.21.3] - 2024-12-25
### Changed
- Allow setting empty lines for `TooltipBuilder` to prevent the tooltip from drawing

## [v21.3.14-1.21.3] - 2024-12-24
### Changed
- Adjust `RangedSliderButton` implementation to no longer require an access widener to help compatibility with the [Cloth Config](https://modrinth.com/mod/cloth-config) mod
- `ClientAbstractions::registerConfigScreenFactory` now merges configs for all provided mod ids instead of overriding the configs of the original mod

## [v21.3.13-1.21.3] - 2024-12-08
### Changed
- Migrate `ExtractRenderStateCallback` to dedicated NeoForge event

## [v21.3.12-1.21.3] - 2024-12-06
### Changed
- Convert some methods back to fields in `InteractionResultHelper`
- Allow `ModLoaderEnvironment::isDevelopmentEnvironment` and `ModLoaderEnvironment::isDevelopmentEnvironmentWithoutDataGeneration` to check any mod id
### Fixed
- Fix `ExplosionEvents$Start` failing to cancel on Fabric

## [v21.3.11-1.21.3] - 2024-11-27
### Changed
- Supply missing registries to `AddDataPackReloadListenersCallback`

## [v21.3.10-1.21.3] - 2024-11-27
### Fixed
- Fix `RegisterFuelValuesCallback` leading to an `OutOfMemoryError` on NeoForge

## [v21.3.9-1.21.3] - 2024-11-27
### Added
- Add additional overloads for `RegistryManager::registerBlockItem`, `RegistryManager::registerBlockEntityType`, and `RegistryManager::registerPoiType`
- Add `ModLoaderEnvironment::isPuzzlesLibDevelopmentEnvironmentWithoutDataGeneration` and `ModLoaderEnvironment::isPuzzlesLibDevelopmentEnvironment`
### Fixed
- Fix `RegistryManager::registerBlockItem` failing to copy a block's name for the corresponding item

## [v21.3.8-1.21.3] - 2024-11-25
### Fixed
- Fix transmute crafting recipes copying all input components

## [v21.3.7-1.21.3] - 2024-11-25
### Fixed
- Fix clearing all effects crashing on Fabric
- Fix `AbstractRecipeProvider` failing to generate some recipes occasionally

## [v21.3.6-1.21.3] - 2024-11-24
### Changed
- Implement missing methods for `AbstractRecipeProvider`

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
