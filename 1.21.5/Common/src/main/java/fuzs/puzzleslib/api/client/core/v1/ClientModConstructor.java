package fuzs.puzzleslib.api.client.core.v1;

import fuzs.puzzleslib.api.client.core.v1.context.*;
import fuzs.puzzleslib.api.core.v1.BaseModConstructor;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.core.ClientFactories;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.apache.logging.log4j.util.Strings;

import java.util.function.Supplier;

/**
 * A base class for a mods main client class, contains a bunch of methods for registering various client content and
 * components.
 */
public interface ClientModConstructor extends BaseModConstructor {

    /**
     * Construct the {@link ClientModConstructor} instance to begin client-side initialization of a mod.
     *
     * @param modId                  the mod id for registering events on Forge to the correct mod event bus
     * @param modConstructorSupplier the main mod instance for mod setup
     */
    static void construct(String modId, Supplier<ClientModConstructor> modConstructorSupplier) {
        if (Strings.isBlank(modId)) throw new IllegalArgumentException("mod id is empty");
        ClientModConstructor modConstructor = modConstructorSupplier.get();
        ModContext modContext = ModContext.get(modId);
        ResourceLocation resourceLocation = ModContext.getPairingIdentifier(modId, modConstructor);
        modContext.scheduleClientModConstruction(resourceLocation, () -> {
            PuzzlesLib.LOGGER.info("Constructing client components for {}", resourceLocation);
            ClientFactories.INSTANCE.constructClientMod(modId, modConstructor);
        });
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
     * @param context register an {@link net.minecraft.client.renderer.entity.EntityRenderer} for an entity
     */
    default void onRegisterEntityRenderers(EntityRenderersContext context) {
        // NO-OP
    }

    /**
     * @param context add a renderer to a block entity
     */
    default void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        // NO-OP
    }

    /**
     * @param context add a client tooltip component to a common tooltip component
     */
    default void onRegisterClientTooltipComponents(ClientTooltipComponentsContext context) {
        // NO-OP
    }

    /**
     * @param context add particle providers for a particle type
     */
    default void onRegisterParticleProviders(ParticleProvidersContext context) {
        // NO-OP
    }

    /**
     * @param context register a screen for a menu type
     */
    default void onRegisterMenuScreens(MenuScreensContext context) {
        // NO-OP
    }

    /**
     * @param context add a layer definition for a {@link ModelLayerLocation}
     */
    default void onRegisterLayerDefinitions(LayerDefinitionsContext context) {
        // NO-OP
    }

    /**
     * @param context add external models to be loaded
     */
    default void onRegisterAdditionalModels(AdditionalModelsContext context) {
        // NO-OP
    }

    /**
     * @param context register a resolver responsible for mapping each {@link BlockState} of a block to an
     *                {@link net.minecraft.client.renderer.block.model.UnbakedBlockStateModel}
     */
    default void onRegisterBlockStateResolver(BlockStateResolverContext context) {
        // NO-OP
    }

    /**
     * @param context register additional renders to run after stack count and durability have been drawn for an item
     *                stack
     */
    default void onRegisterItemDecorations(ItemDecorationsContext context) {
        // NO-OP
    }

    /**
     * @param context register a custom shader that is applied when spectating a certain entity type
     */
    default void onRegisterEntitySpectatorShaders(EntitySpectatorShadersContext context) {
        // NO-OP
    }

    /**
     * @param context register codecs for handling custom item model types and properties
     */
    default void onRegisterItemModels(ItemModelsContext context) {
        // NO-OP
    }

    /**
     * @param context register a codec for a custom {@link SpecialModelRenderer.Unbaked} implementation
     */
    @Deprecated(forRemoval = true)
    default void onRegisterSpecialBlockModelTypes(SpecialBlockModelTypesContext context) {
        // NO-OP
    }

    /**
     * @param context register a custom unbaked special model renderer implementation to be used for statically rendered
     *                blocks, such as blocks visually appearing in minecarts and held by enderman
     */
    default void onRegisterSpecialBlockModelRenderers(SpecialBlockModelRenderersContext context) {
        // NO-OP
    }

    /**
     * @param context register models for custom {@link net.minecraft.world.level.block.SkullBlock.Type}
     *                implementations
     */
    default void onRegisterSkullRenderers(SkullRenderersContext context) {
        // NO-OP
    }

    /**
     * @param context register additional {@link RenderLayer}s for a living entity
     */
    default void onRegisterLivingEntityRenderLayers(LivingEntityRenderLayersContext context) {
        // NO-OP
    }

    /**
     * @param context register a {@link KeyMapping} so it can be saved to and loaded from game options
     */
    default void onRegisterKeyMappings(KeyMappingsContext context) {
        // NO-OP
    }

    /**
     * @param context register custom {@link RenderType}s for blocks
     */
    default void onRegisterBlockRenderTypes(RenderTypesContext<Block> context) {
        // NO-OP
    }

    /**
     * @param context register custom {@link RenderType}s for fluids
     */
    default void onRegisterFluidRenderTypes(RenderTypesContext<Fluid> context) {
        // NO-OP
    }

    /**
     * @param context register block color providers
     */
    default void onRegisterBlockColorProviders(BlockColorsContext context) {
        // NO-OP
    }

    /**
     * @param context register additional resource pack sources
     */
    default void onAddResourcePackFinders(PackRepositorySourcesContext context) {
        // NO-OP
    }

    /**
     * @param context register new resource pack provided shaders
     */
    default void onRegisterCoreShaders(CoreShadersContext context) {
        // NO-OP
    }

    /**
     * @param context register new render buffers to {@link net.minecraft.client.renderer.RenderBuffers}
     */
    default void onRegisterRenderBuffers(RenderBuffersContext context) {
        // NO-OP
    }
}
