# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.6.6-1.21.6] - 2025-06-25
### Added
- Add `DrawItemStackOverlayCallback`, deprecating `ItemDecorationsContext`
### Changed
- Update for NeoForge 21.6.16+
### Fixed
- Fix more status bar height provider issues

## [v21.6.5-1.21.6] - 2025-06-25
### Fixed
- Fix more status bar height provider issues

## [v21.6.4-1.21.6] - 2025-06-25
### Fixed
- Fix more status bar height provider issues

## [v21.6.3-1.21.6] - 2025-06-25
### Added
- Add `ModLoaderEnvironment::isModPresentClientside`
### Fixed
- Fix various status bar height provider issues

## [v21.6.2-1.21.6] - 2025-06-19
### Added
- Add more utility methods to `ContainerSerializationHelper`
### Changed
- Revert attachments to using `Codec` instead of `MapCodec` for serialisation

## [v21.6.1-1.21.6] - 2025-06-18
### Added
- Add `ValueSerializationHelper`
- Add `GuiGraphicsHelper::drawInBatch8xOutline`
### Fixed
- Fix reading field `puzzleslib:spawn_type` on mobs in worlds created before Minecraft 1.21.6 on Fabric
- Fix `GatherEffectScreenTooltipCallback` injection point on Fabric

## [v21.6.0-1.21.6] - 2025-06-18
- Update to Minecraft 1.21.6
### Added
- Add `PictureInPictureRendererContext`
- Add `CommonHelper::getMinecraftServer`
- Add `CodecExtras::fromEnum`
- Add `RegistryManager::registerEnchantmentEffectComponentType`
- Add `ResourceKeyHelper::getResourceLocation`
- Add many enchantment value effect bonus helper methods to `EnchantingHelper`
- Add `ModPackMetadataProvider`
### Changed
- Migrate `GuiLayersContext` to use custom layer implementation
- Rework `FogEvents` to account for vanilla changes
- Simplify `RenderLevelEvents` implementation as `RenderLevelCallback`
- Greatly simplify `ContainerSerializationHelper`
- Replace `AnvilEvents` with a single `CreateAnvilResultCallback`
- Replace `GrindstoneEvents` with a single `CreateGrindstoneResultCallback`
- Move some methods from `LookupHelper` to dedicated classes
- Move `AbstractParticleProvider` & `AbstractEquipmentProvider` to common module
### Removed
- Remove `fuzs.puzzleslib.api.capability.v3`
- Remove `fuzs.puzzleslib.api.network.v3`
- Remove `RenderGuiLayerEvents`
- Remove deprecated `ItemTooltipRegistry` classes
- Remove `RenderTypeHelper`, all functionality is now covered by `RenderTypesContext`
- Remove `ExtendedMenuSupplier`
- Remove `EnchantingHelper::isBookEnchantable`
- Remove `BlockEvents.FarmlandTrample`
- Remove defaulted mutable value classes from public api
- Remove `ComputeEnchantedLootBonusCallback`
- Remove `EnchantingHelper::getMobLootingLevel`
- Remove `ServerEntityLevelEvents.Spawn`
