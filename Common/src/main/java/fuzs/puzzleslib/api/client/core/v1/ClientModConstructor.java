package fuzs.puzzleslib.api.client.core.v1;

import fuzs.puzzleslib.api.client.core.v1.context.*;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.context.AddReloadListenersContext;
import fuzs.puzzleslib.api.core.v1.context.ModLifecycleContext;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.core.ClientFactories;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.apache.logging.log4j.util.Strings;

import java.util.function.Supplier;

/**
 * a base class for a mods main client class, contains a bunch of methods for registering various things
 */
public interface ClientModConstructor {

    /**
     * Construct the {@link ClientModConstructor} instance provided as <code>supplier</code> to begin client-side initialization of a mod.
     *
     * @param modId the mod id for registering events on Forge to the correct mod event bus
     * @param modConstructor       the main mod instance for mod setup
     * @param contentRegistrations specific content this mod uses that needs to be additionally registered
     */
    static void construct(String modId, Supplier<ClientModConstructor> modConstructor, ContentRegistrationFlags... contentRegistrations) {
        if (Strings.isBlank(modId)) throw new IllegalArgumentException("mod id must not be empty");
        // not an issue on Fabric, but Forge might call client construction before common
        ModContext.get(modId).scheduleClientModConstruction(() -> {
            PuzzlesLib.LOGGER.info("Constructing client components for mod {}", modId);
            ClientFactories.INSTANCE.constructClientMod(modId, modConstructor, contentRegistrations);
        });
    }

    /**
     * runs when the mod is first constructed, on the client only really used for registering event callbacks
     */
    default void onConstructMod() {

    }

    /**
     * runs after content has been registered, so it's safe to use here
     * used to set various values and settings for already registered content
     *
     * @param context enqueue work to be run sequentially for all mods as the setup phase runs in parallel on Forge
     */
    default void onClientSetup(final ModLifecycleContext context) {

    }

    /**
     * @param context add a renderer to an entity
     */
    default void onRegisterEntityRenderers(final EntityRenderersContext context) {

    }

    /**
     * @param context add a renderer to a block entity
     */
    default void onRegisterBlockEntityRenderers(final BlockEntityRenderersContext context) {

    }

    /**
     * @param context add a client tooltip component to a common tooltip component
     */
    default void onRegisterClientTooltipComponents(final ClientTooltipComponentsContext context) {

    }

    /**
     * @param context add particle providers for a particle type
     */
    default void onRegisterParticleProviders(final ParticleProvidersContext context) {

    }

    /**
     * @param context add a layer definition for a {@link ModelLayerLocation}
     */
    default void onRegisterLayerDefinitions(final LayerDefinitionsContext context) {

    }

    /**
     * @param context add a search tree builder together with a token
     */
    default void onRegisterSearchTrees(final SearchRegistryContext context) {

    }

    /**
     * @param context Context for modifying baked models right after they've been reloaded.
     */
    default void onModifyBakingResult(final DynamicModifyBakingResultContext context) {

    }

    /**
     * @param context Context for retrieving baked models from the model manager after they've been reloaded.
     */
    default void onBakingCompleted(final DynamicBakingCompletedContext context) {

    }

    /**
     * @param context add external models to be loaded
     */
    default void onRegisterAdditionalModels(final AdditionalModelsContext context) {

    }

    /**
     * @param context register model predicates for custom item models
     */
    default void onRegisterItemModelProperties(final ItemModelPropertiesContext context) {

    }

    /**
     * @param context register a custom inventory renderer for an item belonging to a block entity
     */
    default void onRegisterBuiltinModelItemRenderers(final BuiltinModelItemRendererContext context) {

    }

    /**
     * @param context register additional renders to run after stack count and durability have been drawn for an item stack
     */
    default void onRegisterItemDecorations(final ItemDecorationContext context) {

    }

    /**
     * @param context register a custom shader that is applied when spectating a certain entity type
     */
    default void onRegisterEntitySpectatorShaders(final EntitySpectatorShaderContext context) {

    }

    /**
     * @param context register models for custom {@link net.minecraft.world.level.block.SkullBlock.Type} implementations
     */
    default void onRegisterSkullRenderers(final SkullRenderersContext context) {

    }

    /**
     * @param context adds a listener to the client resource manager to reload at the end of all resources
     */
    default void onRegisterResourcePackReloadListeners(final AddReloadListenersContext context) {

    }

    /**
     * @param context register additional {@link RenderLayer}s for a living entity
     */
    default void onRegisterLivingEntityRenderLayers(final LivingEntityRenderLayersContext context) {

    }

    /**
     * @param context register a {@link KeyMapping} so it can be saved to and loaded from game options
     */
    default void onRegisterKeyMappings(final KeyMappingsContext context) {

    }

    /**
     * @param context register custom {@link RenderType}s for blocks
     */
    default void onRegisterBlockRenderTypes(final RenderTypesContext<Block> context) {

    }

    /**
     * @param context register custom {@link RenderType}s for fluids
     */
    default void onRegisterFluidRenderTypes(final RenderTypesContext<Fluid> context) {

    }

    /**
     * @param context register custom block color providers
     */
    default void onRegisterBlockColorProviders(final ColorProvidersContext<Block, BlockColor> context) {

    }

    /**
     * @param context register custom item color providers
     */
    default void onRegisterItemColorProviders(final ColorProvidersContext<Item, ItemColor> context) {

    }

    /**
     * @param context add items to a creative tab
     */
    default void onBuildCreativeModeTabContents(final BuildCreativeModeTabContentsContext context) {

    }

    /**
     * @param context register additional resource pack sources
     */
    default void onAddResourcePackFinders(final PackRepositorySourcesContext context) {

    }
}
