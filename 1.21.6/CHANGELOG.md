# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
