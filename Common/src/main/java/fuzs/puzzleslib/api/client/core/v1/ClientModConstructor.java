package fuzs.puzzleslib.api.client.core.v1;

import fuzs.puzzleslib.api.client.core.v1.context.*;
import fuzs.puzzleslib.api.core.v1.BaseModConstructor;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

/**
 * A base class for a mods main client class, contains a bunch of methods for registering various client content and
 * components.
 */
public interface ClientModConstructor extends BaseModConstructor {

    /**
     * Construct the {@link ClientModConstructor} instance to begin client-side initialization of a mod.
     *
     * @param modId                  the mod id
     * @param modConstructorSupplier the mod instance for the setup
     */
    static void construct(String modId, Supplier<ClientModConstructor> modConstructorSupplier) {
        construct(ResourceLocation.fromNamespaceAndPath(modId, "client"), modConstructorSupplier);
    }

    /**
     * Construct the {@link ClientModConstructor} instance to begin client-side initialization of a mod.
     *
     * @param resourceLocation       the identifier for the provided mod instance
     * @param modConstructorSupplier the mod instance for the setup
     */
    static void construct(ResourceLocation resourceLocation, Supplier<ClientModConstructor> modConstructorSupplier) {
        ModConstructorImpl.construct(resourceLocation,
                modConstructorSupplier,
                ClientProxyImpl.get()::getClientModConstructorImpl);
    }

    /**
     * Runs when the mod is first constructed, on the client only really used for registering event callbacks.
     */
    default void onConstructMod() {
        // NO-OP
    }

    /**
     * Runs after content has been registered, so it's safe to use here.
     * <p>
     * Used to set various values and settings for already registered content.
     */
    default void onClientSetup() {
        // NO-OP
    }

    /**
     * @param context add a renderer to an entity
     */
    default void onRegisterEntityRenderers(final EntityRenderersContext context) {
        // NO-OP
    }

    /**
     * @param context add a renderer to a block entity
     */
    default void onRegisterBlockEntityRenderers(final BlockEntityRenderersContext context) {
        // NO-OP
    }

    /**
     * @param context add a client tooltip component to a common tooltip component
     */
    default void onRegisterClientTooltipComponents(final ClientTooltipComponentsContext context) {
        // NO-OP
    }

    /**
     * @param context add particle providers for a particle type
     */
    default void onRegisterParticleProviders(final ParticleProvidersContext context) {
        // NO-OP
    }

    /**
     * @param context register a screen for a menu type
     */
    default void onRegisterMenuScreens(final MenuScreensContext context) {
        // NO-OP
    }

    /**
     * @param context add a layer definition for a {@link ModelLayerLocation}
     */
    default void onRegisterLayerDefinitions(final LayerDefinitionsContext context) {
        // NO-OP
    }

    /**
     * @param context add external models to be loaded
     */
    default void onRegisterAdditionalModels(final AdditionalModelsContext context) {
        // NO-OP
    }

    /**
     * @param context register model predicates for custom item models
     */
    default void onRegisterItemModelProperties(final ItemModelPropertiesContext context) {
        // NO-OP
    }

    /**
     * @param context register a custom inventory renderer for an item belonging to a block entity
     */
    default void onRegisterBuiltinModelItemRenderers(final BuiltinModelItemRendererContext context) {
        // NO-OP
    }

    /**
     * @param context register additional renders to run after stack count and durability have been drawn for an item
     *                stack
     */
    default void onRegisterItemDecorations(final ItemDecorationContext context) {
        // NO-OP
    }

    /**
     * @param context register a custom shader that is applied when spectating a certain entity type
     */
    default void onRegisterEntitySpectatorShaders(final EntitySpectatorShaderContext context) {
        // NO-OP
    }

    /**
     * @param context register models for custom {@link net.minecraft.world.level.block.SkullBlock.Type}
     *                implementations
     */
    default void onRegisterSkullRenderers(final SkullRenderersContext context) {
        // NO-OP
    }

    /**
     * @param context register additional {@link RenderLayer}s for a living entity
     */
    @Deprecated
    default void onRegisterLivingEntityRenderLayers(final LivingEntityRenderLayersContext context) {
        // NO-OP
    }

    /**
     * @param context register a {@link KeyMapping} so it can be saved to and loaded from game options
     */
    default void onRegisterKeyMappings(final KeyMappingsContext context) {
        // NO-OP
    }

    /**
     * @param context register custom {@link RenderType}s for blocks
     */
    default void onRegisterBlockRenderTypes(final RenderTypesContext<Block> context) {
        // NO-OP
    }

    /**
     * @param context register custom {@link RenderType}s for fluids
     */
    default void onRegisterFluidRenderTypes(final RenderTypesContext<Fluid> context) {
        // NO-OP
    }

    /**
     * @param context register custom block color providers
     */
    default void onRegisterBlockColorProviders(final ColorProvidersContext<Block, BlockColor> context) {
        // NO-OP
    }

    /**
     * @param context register custom item color providers
     */
    default void onRegisterItemColorProviders(final ColorProvidersContext<Item, ItemColor> context) {
        // NO-OP
    }

    /**
     * @param context register additional resource pack sources
     */
    default void onAddResourcePackFinders(final PackRepositorySourcesContext context) {
        // NO-OP
    }

    /**
     * @param context register new resource pack provided shaders
     */
    default void onRegisterCoreShaders(final CoreShadersContext context) {
        // NO-OP
    }

    /**
     * @param context register new render buffers to {@link net.minecraft.client.renderer.RenderBuffers}
     */
    default void onRegisterRenderBuffers(final RenderBuffersContext context) {
        // NO-OP
    }
}
