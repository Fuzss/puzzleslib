# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.0.0-1.21] - 2024-07-04
- Port to Minecraft 1.21
- Forge is no longer support in favor of NeoForge
### Added
- Add `FinalizeItemComponentsCallback`
- Add `ResourceLocationHelper`
- Add `EntityTickEvents` in favor of `LivingTickCallback`
- Add `RegisterPotionBrewingMixesCallback` in favor of `PotionBrewingRegistry`
### Changed
- Overhaul `RenderGuiLayerEvents` ids
- Expand `ScreenTooltipFactory` to allow for directly setting the tooltip on a widget
- Migrate `ItemAttributeModifiersCallback` to `ComputeItemAttributeModifiersCallback`
- Migrate `ArmorMaterial` helpers from `ItemEquipmentFactories` to `RegistryManager`
- Migrate `CopyTagRecipe` to `CopyComponentsRecipe`
### Removed
- Remove old `ScreenHelper` in favor of access widener
- Remove `NetworkHandlerV2`, `MessageV2` is now compatible with `MessageV3` and can therefore be registered via `NetworkHandlerV2`
- Remove remaining Cardinal Components classes
