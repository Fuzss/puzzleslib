# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v8.1.27-1.20.1] - 2025-03-04
### Added
- Backport Capability System v3 from 1.20.4 version that no longer requires the Cardinal Components mod
### Changed
- Update Fabric Loom to v1.9

## [v8.1.26-1.20.1] - 2025-03-04
### Changed
- Include [Mixin Extras](https://github.com/LlamaLad7/MixinExtras) library

## [v8.1.25-1.20.1] - 2024-11-15
### Fixed
- Fix crash due to out of bounds index in `TooltipRenderHelper::createClientComponents`

## [v8.1.24-1.20.1] - 2024-10-04
### Fixed
- Fix `DynamicPackResources` failing to generate some resources

## [v8.1.23-1.20.1] - 2024-09-09
### Fixed
- Fix `DynamicPackResources` providing an invalid pack when dynamic data generation fails

## [v8.1.22-1.20.1] - 2024-08-26
### Fixed
- Fix item model display overrides being able to compute before models have been reloaded on Fabric

## [v8.1.21-1.20.1] - 2024-06-26
### Fixed
- Fix `PlayerInteractEvents$AttackBlock` not firing for creative players on Fabric

## [v8.1.20-1.20.1] - 2024-05-26
### Fixed
- Attempt to improve handling for missing child reload listeners during resource reloading

## [v8.1.19-1.20.1] - 2024-05-09
### Changed
- Update mod list printing to better reflect child mod relations (backport from 1.20.4)
### Fixed
- Fix crash involving `GatherPotentialSpawnsCallback` when the ModernFix mod is installed on Forge

## [v8.1.18-1.20.1] - 2024-03-20
### Fixed
- Fix `LivingDeathCallback` not running for players on Fabric

## [v8.1.17-1.20.1] - 2024-02-16
### Fixed
- Fix item model overrides not applying on Forge

## [v8.1.16-1.20.1] - 2024-02-01
### Fixed
- Fix crash when breaking crops with Farmer's Delight's sickle

## [v8.1.15-1.20.1] - 2024-01-28
### Fixed
- Fix `TagsUpdatedCallback` & `ServerLifecycleEvents.Starting` not running on dedicated servers

## [v8.1.14-1.20.1] - 2024-01-24
### Changed
- Delay event initialization to avoid being blamed for class loading errors
### Fixed
- Fix killing entities with custom damage sources not yielding any experience
- Fix compatibility with Hephaestus by moving problematic code to the relevant mod, so only that one mod is incompatible until the issues is properly resolved on Hephaestus' end

## [v8.1.13-1.20.1] - 2024-01-11
### Fixed
- Fixed unloading of a server config placed in a world's `serverconfig` directory failing and causing some processes to hang

## [v8.1.12-1.20.1] - 2024-01-05
### Fixed
- Fixed missing `ToolTypeHelper::isTrident` method

## [v8.1.11-1.20.1] - 2023-12-03
### Fixed
- Fixed anonymous network handlers allowing for duplicate ids

## [v8.1.10-1.20.1] - 2023-12-03
### Changed
- Use string contexts for network handler ids instead of numeric

## [v8.1.9-1.20.1] - 2023-11-19
### Changed
- Minor capability related code cleanup
- Error messages from `BlockConversionHelper` now include more context
### Fixed
- Fixed `RegistryEntryAddedCallback` not being synchronized correctly

## [v8.1.8-1.20.1] - 2023-11-07
### Fixed
- Implement a temporary workaround for screens crashing the game when opened with [Controlify](https://www.curseforge.com/minecraft/mc-mods/controlify) installed

## [v8.1.7-1.20.1] - 2023-11-07
### Added
- Added `ComponentHelper` for converting various text instances back to `Component`
- Added `LivingEquipmentChangeCallback`
### Changed
- Improve parity for `BlockEvents.Break`, `LivingAttackCallback`, and `PlayerInteractEvents.AttackBlock` between different mod loaders
### Fixed
- Ensure backwards compatibility of `NetworkHandlerV3`

## [v8.1.6-1.20.1] - 2023-10-29
### Changed
- Moved methods for registering block and fluid render types to `ClientAbstractions`
### Fixed
- Fixed a crash with Forgified Fabric Api due to a faulty Mixin in `ModelProvider`

## [v8.1.5-1.20.1] - 2023-10-29
### Changed
- Adjusted `ModelEvents` to work with ModernFix's dynamic resources

## [v8.1.4-1.20.1] - 2023-10-28
### Fixed
- Fixed chunk events chunk type

## [v8.1.3-1.20.1] - 2023-10-28
### Changed
- Disabled `LivingEvents$Breathe` and `LivingEvents$Drown` to be able to lower the required Forge version to 47.1.3
- Added a way in `DataProviderHelper` to support legacy data providers
- Refactored some parts of `NetworkHandlerV3`
- Be more careful when firing `RegistryEntryAddedCallback` by introducing a lock and catching possible exceptions to avoid the whole registration process from failing
- Ensure exceptions thrown during model events are caught and no longer crash the game
### Fixed
- Fixed dynamic data generation failing on Windows

## [v8.1.2-1.20.1] - 2023-10-27
### Fixed
- Set required Forge version so that NeoForge is still supported

## [v8.1.1-1.20.1] - 2023-10-26
### Fixed
- Fixed event classes possibly being loaded too early under some circumstances, resolves crashes on start-up when installed together with e.g. Stylish Effects
- Fixed required Forge version being incorrectly set (47.2.0 is required now, not 47.1.0)
- Fixed Puzzles Lib crashing the game on Forge when there are any unmet mod version requirements, preventing the Forge screen informing the user from showing

## [v8.1.0-1.20.1] - 2023-10-26
- Public release of all versions that have come after v8.0.24
### Changed
- Expanded `AbstractModelProvider`

## [v8.0.41-1.20.1] - 2023-10-24
### Added
- Added common helper methods for obtaining `RenderType`s
- Added `BlockConversionHelper` from merging various block related utility methods

## [v8.0.40-1.20.1] - 2023-10-23
### Changed
- Refined performance for new model events on Forge

## [v8.0.39-1.20.1] - 2023-10-22
### Added
- Added `ModifyUnbakedModel`, `ModifyBakedModel` and `AdditionalBakedModel` for finer control over models based entirely on mod loader specific implementations
- Added helper methods for setting `BlockItem` blocks
### Changed
- Providing the `Minecraft` instance is no longer required in `ClientAbstractions`
- `RegistryEntryAddedCallback` now includes the current registry
- Deprecated old model events
### Fixed
- Fixed `RegistryEntryAddedCallback` running too early on Forge, leading to problems with implementations relying on content registered during the event

## [v8.0.38-1.20.1] - 2023-10-20
### Added
- Added `GatherDebugTextEvents$Left` and `GatherDebugTextEvents$Right`
- Added `LivingEvents$Breathe` and `LivingEvents$Drown`
- Added `RegistryEntryAddedCallback`
- Added `ServerChunkEvents$Watch` and `ServerChunkEvents$Unwatch`
### Changed
- Allow for overriding global server configs when a local server config is present per world
- Allow for creating common events that do not necessarily require a mod loader specific event to be implemented
### Fixed
- Fixed an error being logged on Quilt when unsuccessfully trying to access to Fabric internal field required for the biome modifications api
- Fixed a crash when trying to deserialize invalid `ResourceLocation`s provided by config entries

## [v8.0.37-1.20.1] - 2023-10-04
### Changed
- No longer set `ExistingFileHelper` on data providers during Forge data generation
- A few new `null` checks in `AbstractLanguageProvider`

## [v8.0.36-1.20.1] - 2023-10-01
### Changed
- Reverted experimental changes to `AbstractModelProvider`

## [v8.0.35-1.20.1] - 2023-09-29
### Added
- Added `ForgeDataProviderContext$Factory` to better support data generation on Forge
### Changed
- Some experimental changes to `AbstractModelProvider` to hopefully fix runtime on Forge mods
- `ConfigDataSet` can now exclude entries by marking them with `!`

## [v8.0.34-1.20.1] - 2023-09-28
### Added
- Added `BlockEvents$Break` and `BlockEvents$DropExperience`
- Added `TickingBlockEntity` and `TickingEntityBlock` helper interfaces for creating ticking block entities without the need to use static ticker methods
- Added `AddToastCallback` for handling client-side toasts
- Added `ScreenEvents$BeforeInitV2` and `ScreenEvents$AfterInitV2` using type parameters for the screen instance
- Added `CommonAbstractions::createPackInfo`
- Added `ModConstructor::onRegisterBlockInteractions` for registering block conversions such as strippable logs or tillable dirt blocks
- Added `GrindstoneEvents$Update` and `GrindstoneEvents$Use`
- Added `TypedTagFactory` and `BoundTagFactory` for creating new `TagKey`s, tag keys have therefore been removed from the new `RegistryManager`
- Added `CombinedIngredients` for creating more complex instances of `Ingredient`
- Added `ShapesHelper`, mainly useful for rotating a `VoxelShape` to a given `Direction`
- Added `DynamicPackResources` for generating resources via data providers at runtime
- Added `RegistryHelper` for various vanilla registry related helper methods 
### Changed
- The Puzzles Access Api mod is now bundled with Puzzles Lib
- Overhauled data generation classes, moving and reworking them for the common project to be usable in the new `DynamicPackResources`
- Refined `AbstractModPackResources` to better handle mod ids as well as allow for hiding a pack, a Forge exclusive feature
- A mod providing biome modifications on Forge no longer needs to include its own biome modifier, the file is now automatically generated using a built-in data pack
- Overhauled internal implementation of `ItemAttributeModifiersCallback`
### Fixed
- Fixed a class loading issue related to `ItemModelDisplayOverrides` and `EventInvokerRegistry` implementations
- Fixed `AbstractParticleDescriptionProvider` not verifying the existence of used textures
- Fixed an issue where the current game server wouldn't be properly stored on Fabric

## [v8.0.33-1.20.1] - 2023-09-09
### Changed
- Reworked reload listener handling introduced in the last version to fix issues with mod loading getting stuck due to reload listeners that have not been properly completed
- Changed handling of internal ids for multiple network handlers using the same namespace

## [v8.0.32-1.20.1] - 2023-09-08
### Added
- Added `ClientParticleTypes` for registering fully client-side particle providers
### Changed
- `ContentRegistrationFlags` are now enabled via implementing `BaseModConstructor::getContentRegistrationFlags`
- Fabric is now more strict in enforcing `ContentRegistrationFlags` even when not strictly required to improve parity with Forge
- Greatly expanded upon `ParticleProvidersContext` to allow for more kinds of particle types to be registered
- Overhauled `ForwardingReloadListener` to support forwarding a collection of `PreparableReloadListener` instances instead of just a single one
- Refactored `FabricReloadListener` into a more general `FabricReloadListenerHelper` class

## [v8.0.31-1.20.1] - 2023-09-07
### Added
- Add access widener for `EntityRenderDispatcher#renderers` on Fabric

## [v8.0.30-1.20.1] - 2023-09-05
### Added
- Puzzles Lib now prints a list of all installed mods to the log on start-up in an effort to help diagnose issues when no crash-report has been generated or provided
- This feature is only enabled on Forge, as Fabric Loader already does just that by default
### Changed
- Refactored some mod loader specific code related to mod list entries
### Fixed
- Fixed `LivingDeathCallback` only running on the server-side on Fabric

## [v8.0.29-1.20.1] - 2023-09-05
### Added
- Added `RenderLivingEvents`

## [v8.0.28-1.20.1] - 2023-09-04
### Changed
- Deprecated `DistType`, this includes some refactors in `NetworkHandlerV2`
### Fixed
- Fixed client-side interaction events potentially running after common interaction events on Fabric

## [v8.0.27-1.20.1] - 2023-09-02
### Added
- Added a helper system for checking if a mod is installed on the server-side

## [v8.0.26-1.20.1] - 2023-08-19
### Added
- Added a new separate `RegistryManager` that returns instances of `Holder.Reference` instead of our own `RegistryReference` implementation to be more inline with the vanilla registration system

## [v8.0.25-1.20.1] - 2023-08-17
### Added
- Added `InteractionInputEvents$AttackV2`, `InteractionInputEvents$Use` and `InteractionInputEvents$Pick`
- Added `PlayerInteractEvents$AttackBlockV2` and `PlayerInteractEvents$UseItemV2`
- Added a few new helper methods to `ScreenHelper`
### Changed
- Cleaned up small parts to the event system to reduce duplicate code
- Revised implementation of all `PlayerInteractEvents` to match much more closely between Forge and Fabric
- `ServerEntityLevelEvents$LoadV2` now fires for all loaded entities, not just when they are loaded from chunk storage, but also when they are freshly spawned in
### Fixed
- Fixed brewing recipes not showing in some recipe viewer mods on Forge

## [v8.0.24-1.20.1] - 2023-08-16
### Changed
- Reworked implementation of `LivingDropsCallback` on Fabric to make it less likely for loot modifications from other mods to completely break all loot for a mob
### Fixed
- Fixed `ServiceProviderHelper` not loading as early as it should
- Removed debug code related to testing out new key binding features

## [v8.0.23-1.20.1] - 2023-08-14
### Added
- Added `CommonAbstractions::getMobSpawnType`
- Some refactors to `ServerEntityLevelEvents`, including the addition of `ServerEntityLevelEvents$Spawn`
### Fixed
- Fixed `ConfigDataSet` failing to dissolve when duplicate values are present

## [v8.0.22-1.20.1] - 2023-08-14
### Added
- Added `GatherPotentialSpawnsCallback`
- Added `ServerEntityLevelEvents$LoadV2`, `ServerEntityLevelEvents$Load` is now deprecated
- Added `CheckMobDespawnCallback`
- Added `EntityRidingEvents$Start` and `EntityRidingEvents$Stop`
- Added `CoreShadersContext` to `ClientModConstructor` for registering built-in shaders
- Added `KeyActivationContext` to helper with registering keybinds that only work when a screen is open or while playing (no screen is open)
### Changed
- Overhauled `GameRulesFactory` to add many convenient overloads, as well as cleaning up the Forge implementation
- A few new methods in `AbstractLanguageProvider` for creative tabs and game rules
- Adjusted `ScreenTooltipFactory` to no longer require a `Font` instance
- Refactored `KeyMappingsContext` to require a `KeyActivationContext` instance
- Reworked methods for registering new `PoiType`s in `RegistryManager` to no longer require a custom `PoiTypeBuilder` instance
- Migrate some internals on Fabric to Fabric Api's new model loading api
### Fixed
- Fixed `ScreenTooltipFactory` removing empty lines from tooltips

## [v8.0.21-1.20.1] - 2023-08-12
### Fixed
- Fixed start-up crash when the [Emojiful](https://www.curseforge.com/minecraft/mc-mods/emojiful) mod is installed

## [v8.0.20-1.20.1] - 2023-08-12
### Fixed
- Fixed implementation errors in `UseItemEvents.Tick` on Fabric which would prevent successfully using items like bows and tridents

## [v8.0.19-1.20.1] - 2023-08-11
### Added
- Added `TagProviderExtension`

## [v8.0.18-1.20.1] - 2023-08-10
### Added
- Added `GameRenderEvents.Before` and `GameRenderEvents.After`
- Added helper methods to `ClientAbstractions` for getting `partialTick` and `SearchRegistry`
### Changed
- Deprecated `SearchRegistryContext`

## [v8.0.17-1.20.1] - 2023-08-04
### Changed
- Custom smithing upgrade recipes without a smithing template are now automatically turned into crafting recipes with the same items and functionality to be visible in recipe viewers like JEI without having to provide dedicated support

## [v8.0.16-1.20.1] - 2023-08-01
### Fixed
- Fixed `LivingHurtCallback` not firing for players on Fabric
- Fixed `UseItemEvents$Tick` always having a wrong `useDuration` value
- Fixed `PlayerInteractEvents$UseBlock` and `PlayerInteractEvents$UseItem` not sending packets to the server for successful interactions if a result other than `InteractionResult#SUCCESS` is returned from the callback

## [v8.0.15-1.20.1] - 2023-07-30
### Fixed
- Reworked `ServerEntityLevelEvents$Load` and `ClientEntityLevelEvents$Load` on Fabric to prevent a potential memory leak when trying to prevent an entity from loading in

## [v8.0.14-1.20.1] - 2023-07-30
### Fixed
- Fixed an issue with the `Sheets` class accidentally being accessed too early, preventing modded materials such as wood types from registering correctly, which would lead to crashes when e.g. rendering signs

## [v8.0.13-1.20.1] - 2023-07-26
### Added
- Added `ScreenTooltipFactory`

## [v8.0.12-1.20.1] - 2023-07-23
### Fixed
- Fixed a crash when calling `ModelEvents` on Forge

## [v8.0.11-1.20.1] - 2023-07-23
### Added
- Added `RenderHighlightCallback`
- Added `RenderLevelEvents`
### Changed
- Shifted injection points for `InventoryMobEffectsCallback` to be compatible with the [Just Enough Items](https://www.curseforge.com/minecraft/mc-mods/jei) mod on Fabric
- Slightly changed some injection point shifts related to `AnimalTameCallback`

## [v8.0.10-1.20.1] - 2023-07-15
### Added
- Added `ScreenTooltipEvents.Render`
- Added two helper methods for getting `mouseX` and `mouseY` to `ScreenHelper`

## [v8.0.9-1.20.1] - 2023-07-12
### Added
- Added `RenderBlockOverlayCallback`
- Added `FogEvents$Render` and `FogEvents$ComputeColor`
### Fixed
- Fixed `ModelEvents` crashing on Fabric

## [v8.0.8-1.20.1] - 2023-07-06
### Changed
- Migrate `ModelEvents` to common event system

## [v8.0.7-1.20.1] - 2023-07-02
### Fixed
- Fixed a sound related crash on Fabric

## [v8.0.6-1.20.1] - 2023-07-02
### Fixed
- Fixed new mod loading architecture not allowing for multiple config handlers per mod

## [v8.0.5-1.20.1] - 2023-07-02
### Fixed
- Fixed a crash when rendering items on Fabric

## [v8.0.4-1.20.1] - 2023-07-01
### Changed
- Once again invert `ItemDisplayContext`s when registering item model perspective overrides

## [v8.0.3-1.20.1] - 2023-07-01
### Added
- Added `LoadCompleteCallback`
### Changed
- Internal changes to allow for more versatile mod loading
- Separate item model perspective now use built-in features on Forge and longer require a core mod there

## [v8.0.2-1.20.1] - 2023-06-29
### Fixed
- Re-upload to fix failed reobfuscation on Forge

## [v8.0.1-1.20.1] - 2023-06-29
### Added
- Added `DispenseBehaviorsContext`
### Changed
- `ModConstructor::onCommonSetup` and `ClientModConstructor::onClientSetup` now always run deferred on Forge, eliminating the previous `ModLifecycleContext` argument
### Fixed
- A few possible `java.util.ConcurrentModificationException`s during start-up are now prevented by running setup tasks deferred on Forge

## [v8.0.0-1.20.1] - 2023-06-25
- Ported to Minecraft 1.20.1

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
