# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.6.0-1.21.6] - 2025-06-03
- Update to Minecraft 1.21.6
### Changed
- Migrate `GuiLayersContext` to use custom layer implementation
- Rework `FogEvents` to account for vanilla changes
- Simplify `RenderLevelEvents` implementation as `RenderLevelCallback`
- Greatly simplify `ContainerSerializationHelper`
- Replace `AnvilEvents` with a single `CreateAnvilResultCallback`
### Removed
- Remove `fuzs.puzzleslib.api.capability.v3`
- Remove `fuzs.puzzleslib.api.network.v3`
- Remove `RenderGuiLayerEvents`
- Remove deprecated `ItemTooltipRegistry` classes
- Remove `RenderTypeHelper`, all functionality is now covered by `RenderTypesContext`
- Remove `ExtendedMenuSupplier`
- Remove `EnchantingHelper::isBookEnchantable`
- Remove `BlockEvents$FarmlandTrample`
