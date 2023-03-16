package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.client.init.builder.ModSpriteParticleRegistration;
import fuzs.puzzleslib.client.renderer.DynamicBuiltinModelItemRenderer;
import fuzs.puzzleslib.client.renderer.blockentity.SkullRenderersFactory;
import fuzs.puzzleslib.client.renderer.entity.DynamicItemDecorator;
import fuzs.puzzleslib.core.ContentRegistrationFlags;
import fuzs.puzzleslib.core.ModConstructor;
import fuzs.puzzleslib.impl.client.core.ClientFactories;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * a base class for a mods main client class, contains a bunch of methods for registering various things
 */
public interface ClientModConstructor {

    /**
     * this is very much unnecessary as the method is only ever called from loader specific code anyway which does have
     * access to the specific mod constructor, but for simplifying things and having this method in a common place we keep it here
     *
     * @param modId the mod id for registering events on Forge to the correct mod event bus
     * @param modConstructor       the main mod instance for mod setup
     * @param contentRegistrations specific content this mod uses that needs to be additionally registered
     */
    static void construct(String modId, Supplier<ClientModConstructor> modConstructor, ContentRegistrationFlags... contentRegistrations) {
        ClientFactories.INSTANCE.constructClientMod(modId, modConstructor, contentRegistrations);
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
    default void onClientSetup(final ModConstructor.ModLifecycleContext context) {

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
     * @param context context for registering a listener that runs right after baked models have been reloaded
     */
    default void onRegisterModelBakingListeners(final ModelBakingListenersContext context) {

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
    default void onRegisterClientReloadListeners(final ClientReloadListenersContext context) {

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
     * register a renderer for an entity
     */
    @FunctionalInterface
    interface EntityRenderersContext {

        /**
         * registers an {@link net.minecraft.client.renderer.entity.EntityRenderer} for a given entity
         *
         * @param entityType entity type token to render for
         * @param entityRendererProvider entity renderer provider
         * @param <T> type of entity
         */
        <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider);
    }

    /**
     * register a renderer for a block entity
     */
    @FunctionalInterface
    interface BlockEntityRenderersContext {

        /**
         * registers an {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderer} for a given block entity
         *
         * @param blockEntityType             block entity type token to render for
         * @param blockEntityRendererProvider   block entity renderer provider
         * @param <T> type of entity
         */
        <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider);
    }

    /**
     * register a client-side tooltip component factory
     */
    @FunctionalInterface
    interface ClientTooltipComponentsContext {

        /**
         * register custom tooltip components
         *
         * @param type common {@link TooltipComponent} class
         * @param factory factory for creating {@link ClientTooltipComponent} from <code>type</code>
         * @param <T>     type of common component
         */
        <T extends TooltipComponent> void registerClientTooltipComponent(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory);
    }

    /**
     * register a particle provider for a particle type
     */
    interface ParticleProvidersContext {

        /**
         * registers a factory for a particle type client side
         *
         * @param type     particle type (registered separately)
         * @param provider particle factory
         * @param <T>      type of particle
         */
        <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> type, ParticleProvider<T> provider);

        /**
         * registers a factory for a particle type client side
         *
         * @param type     particle type (registered separately)
         * @param factory particle factory
         * @param <T>      type of particle
         */
        <T extends ParticleOptions> void registerParticleFactory(ParticleType<T> type, ModSpriteParticleRegistration<T> factory);
    }

    /**
     * register layer definitions for entity models
     */
    @FunctionalInterface
    interface LayerDefinitionsContext {

        /**
         * registers a new layer definition (used for entity model parts)
         *
         * @param layerLocation model location
         * @param supplier      layer definition supplier
         */
        void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier);
    }

    /**
     * register search tree to private registry in Minecraft singleton
     */
    @FunctionalInterface
    interface SearchRegistryContext {

        /**
         * registers a search tree to {@link SearchRegistry} in {@link net.minecraft.world.entity.vehicle.Minecart}
         *
         * @param searchRegistryKey     the search tree token
         * @param treeBuilder           builder supplier for search tree
         * @param <T>                   type to be searched for
         */
        <T> void registerSearchTree(SearchRegistry.Key<T> searchRegistryKey, SearchRegistry.TreeBuilderSupplier<T> treeBuilder);
    }

    /**
     * get access to registering additional models
     */
    @FunctionalInterface
    interface AdditionalModelsContext {

        /**
         * register a model that is referenced nowhere and would normally not be loaded
         *
         * @param model     the models location
         */
        void registerAdditionalModel(ResourceLocation model);
    }

    /**
     * context for registering a listener that runs right after baked models have been reloaded
     */
    @FunctionalInterface
    interface ModelBakingListenersContext {

        /**
         * register a reload listener
         *
         * @param modelBakingContext    action that runs everytime baked models are reloaded
         */
        void registerReloadListener(DynamicModelBakingContext modelBakingContext);
    }

    /**
     * Context for modifying baked models right after they've been loaded.
     */
    @FunctionalInterface
    interface DynamicModelBakingContext {

        /**
         * Pass reloaded model related instances.
         *
         * @param modelManager      the model manager
         * @param models            map of all baked models, useful to add or replace models
         * @param modelBakery       the bakery
         */
        void onModelBakingCompleted(ModelManager modelManager, Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery);
    }

    /**
     * register model predicates for custom item models
     */
    interface ItemModelPropertiesContext {

        /**
         * register a predicate for all items
         *
         * @param identifier          predicate name
         * @param function      handler for this predicate
         */
        void registerGlobalProperty(ResourceLocation identifier, ClampedItemPropertyFunction function);

        /**
         * register a predicate for an <code>item</code>
         *
         * @param identifier          predicate name
         * @param function      handler for this predicate
         * @param object         item to apply the model property to
         * @param objects         more items to apply the model property to
         */
        void registerItemProperty(ResourceLocation identifier, ClampedItemPropertyFunction function, ItemLike object, ItemLike... objects);
    }

    /**
     * register a custom inventory renderer for an item belonging to a block entity
     */
    @FunctionalInterface
    interface BuiltinModelItemRendererContext {

        /**
         * register a <code>renderer</code> for an <code>item</code>
         *
         * @param renderer  dynamic implementation of {@link net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer}
         * @param object      the item to register for
         * @param objects      more items to register for
         */
        void registerItemRenderer(DynamicBuiltinModelItemRenderer renderer, ItemLike object, ItemLike... objects);
    }

    /**
     * register additional renders to run after stack count and durability have been drawn for an item stack
     */
    @FunctionalInterface
    interface ItemDecorationContext {

        /**
         * register a {@link DynamicItemDecorator} for an <code>item</code>
         *
         * @param decorator         renderer implementation
         * @param object              the item to draw for
         * @param objects              more items to draw for
         */
        void registerItemDecorator(DynamicItemDecorator decorator, ItemLike object, ItemLike... objects);
    }

    /**
     * register a custom shader that is applied when spectating a certain entity type
     */
    @FunctionalInterface
    interface EntitySpectatorShaderContext {

        /**
         * Register the custom shader.
         *
         * @param shaderLocation location to the shader file, usually <code>shaders/post/&lt;file&gt;.json</code>
         * @param object the entity type being spectated
         * @param objects more entity types being spectated
         */
        void registerSpectatorShader(ResourceLocation shaderLocation, EntityType<?> object, EntityType<?>... objects);
    }

    /**
     * register models for custom {@link net.minecraft.world.level.block.SkullBlock.Type} implementations
     */
    interface SkullRenderersContext {

        /**
         * add models for specific skull types
         *
         * @param factory factory for the model(s)
         */
        void registerSkullRenderer(SkullRenderersFactory factory);
    }

    /**
     * adds a listener to the client resource manager to reload at the end of all resources
     *
     * <p>(the resource manager uses a list for keeping track, so it's pretty safe to assume it'll load after vanilla,
     * Fabric has a very limited way of setting some sort of resource dependencies,
     * but they don't work for most stuff and Forge doesn't have them anyway)
     */
    @FunctionalInterface
    interface ClientReloadListenersContext {

        /**
         * register a {@link PreparableReloadListener}
         *
         * @param id                    id of this listener for identifying, only used on Fabric
         * @param reloadListener        the reload-listener to add
         */
        void registerReloadListener(String id, PreparableReloadListener reloadListener);
    }

    /**
     * register additional {@link RenderLayer}s for a living entity, supports players like any other entity
     */
    @FunctionalInterface
    interface LivingEntityRenderLayersContext {


        /**
         * register the additional layer
         *
         * @param entityType        entity type to register for
         * @param factory           the new layer factory
         * @param <E>               the entity type
         * @param <T>               entity type used for the model, should only really be different for players
         * @param <M>               the entity model
         */
        <E extends LivingEntity, T extends E, M extends EntityModel<T>> void registerRenderLayer(EntityType<E> entityType, BiFunction<RenderLayerParent<T, M>, EntityRendererProvider.Context, RenderLayer<T, M>> factory);
    }

    /**
     * register a {@link KeyMapping} so it can be saved to and loaded from game options
     */
    @FunctionalInterface
    interface KeyMappingsContext {

        /**
         * Forge supports much more here for the key mapping (like conflicts, and modifiers, but we keep it simple for the sake of Fabric)
         *
         * @param keyMappings the key mappings to register
         */
        void registerKeyMappings(KeyMapping... keyMappings);
    }

    /**
     * Register custom {@link RenderType}s for blocks and fluids.
     *
     * @param <T> object type supported by provider, either {@link Block} or {@link Fluid}
     */
    @FunctionalInterface
    interface RenderTypesContext<T> {

        /**
         * Register a <code>renderType</code> for an <code>object</code>
         *
         * @param renderType the {@link RenderType} for <code>object</code>
         * @param object object type supporting render type, either {@link Block} or {@link Fluid}
         * @param objects more object types supporting render type, either {@link Block} or {@link Fluid}
         */
        @SuppressWarnings("unchecked")
        void registerRenderType(RenderType renderType, T object, T... objects);
    }

    /**
     * Register custom item/block color providers, like tint getters for leaves or grass.
     *
     * @param <T> provider type, either {@link BlockColor} or {@link ItemColor}
     * @param <P> object type supported by provider, either {@link Block} or {@link Item}
     */
    interface ColorProvidersContext<T, P> {

        /**
         * Register a new <code>provider</code> for a number of <code>objects</code>.
         *
         * @param provider provider type, either {@link BlockColor} or {@link ItemColor}
         * @param object object type supported by provider, either {@link Block} or {@link Item}
         * @param objects more object types supported by provider, either {@link Block} or {@link Item}
         */
        @SuppressWarnings("unchecked")
        void registerColorProvider(P provider, T object, T... objects);

        /**
         * Provides access to already registered providers, might be incomplete during registration,
         * but is good to use in either {@link BlockColor} or {@link ItemColor}.
         *
         * @return access to {@link net.minecraft.client.color.block.BlockColors} or {@link net.minecraft.client.color.item.ItemColors}
         */
        P getProviders();
    }

    /**
     * Add items to a creative tab.
     */
    interface BuildCreativeModeTabContentsContext {

        /**
         * Add items to a creative tab referenced by internal id.
         *
         * @param identifier       the creative mode tab to add items to
         * @param displayItemsGenerator context for adding items to the creative mode tab
         */
        void registerBuildListener(ResourceLocation identifier, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator);

        /**
         * Add items to a creative tab referenced by instance.
         *
         * @param creativeModeTab       the creative mode tab to add items to
         * @param displayItemsGenerator context for adding items to the creative mode tab
         */
        void registerBuildListener(CreativeModeTab creativeModeTab, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator);
    }
}
