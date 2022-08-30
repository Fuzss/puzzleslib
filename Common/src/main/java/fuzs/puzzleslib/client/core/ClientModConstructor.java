package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.client.init.builder.ModScreenConstructor;
import fuzs.puzzleslib.client.init.builder.ModSpriteParticleRegistration;
import fuzs.puzzleslib.client.renderer.DynamicBuiltinModelItemRenderer;
import fuzs.puzzleslib.client.renderer.entity.DynamicItemDecorator;
import fuzs.puzzleslib.client.resources.model.DynamicModelBakingContext;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
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
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * a base class for a mods main client class, contains a bunch of methods for registering various things
 */
public interface ClientModConstructor {

    /**
     * runs when the mod is first constructed, on the client only really used for registering event callbacks
     */
    default void onConstructMod() {

    }

    /**
     * runs after content has been registered, so it's safe to use here
     * used to set various values and settings for already registered content
     */
    default void onClientSetup() {

    }

    /**
     * @param context add a renderer to an entity
     */
    default void onRegisterEntityRenderers(EntityRenderersContext context) {

    }

    /**
     * @param context add a renderer to a block entity
     */
    default void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {

    }

    /**
     * @param context add a client tooltip component to a common tooltip component
     */
    default void onRegisterClientTooltipComponents(ClientTooltipComponentsContext context) {

    }

    /**
     * @param context add particle providers for a particle type
     */
    default void onRegisterParticleProviders(ParticleProvidersContext context) {

    }

    /**
     * @param context register a screen for a menu type
     */
    default void onRegisterMenuScreens(MenuScreensContext context) {

    }

    /**
     * @param context add a sprite to a texture atlas
     */
    default void onRegisterAtlasSprites(AtlasSpritesContext context) {

    }

    /**
     * @param context add a layer definition for a {@link ModelLayerLocation}
     */
    default void onRegisterLayerDefinitions(LayerDefinitionsContext context) {

    }

    /**
     * @param context add a search tree builder together with a token
     */
    default void onRegisterSearchTrees(SearchRegistryContext context) {

    }

    /**
     * context for modifying baked models right after they've been loaded
     *
     * @param modelManager      the model manager
     * @param models            map of all baked models, useful to add or replace models
     * @param modelBakery       the bakery
     *
     * @deprecated              use {@link #onLoadModels(LoadModelsContext)}
     */
    @Deprecated(forRemoval = true)
    default void onLoadModels(ModelManager modelManager, Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery) {

    }

    /**
     * @param context           context for modifying baked models right after they've been loaded
     *
     * @deprecated              use {@link #onRegisterModelBakingCompletedListeners}
     */
    @Deprecated(forRemoval = true)
    default void onLoadModels(LoadModelsContext context) {
        this.onLoadModels(context.modelManager(), context.models(), context.modelBakery());
    }

    /**
     * @param context context for registering a listener that runs right after baked models have been reloaded
     */
    default void onRegisterModelBakingCompletedListeners(ModelBakingCompletedListenersContext context) {
        context.registerReloadListener(c -> this.onLoadModels(new LoadModelsContext(c.modelManager, c.models, c.modelBakery)));
    }

    /**
     * @param context add external models to be loaded
     */
    default void onRegisterAdditionalModels(AdditionalModelsContext context) {

    }

    /**
     * @param context register model predicates for custom item models
     */
    default void onRegisterItemModelProperties(ItemModelPropertiesContext context) {

    }

    /**
     * @param context   register a custom inventory renderer for an item belonging to a block entity
     *
     * @deprecated      use {@link #onRegisterBuiltinModelItemRenderers}
     */
    @Deprecated(forRemoval = true)
    default void onRegisterBuiltinModelItemRenderer(BuiltinModelItemRendererContext context) {

    }

    /**
     * @param context register a custom inventory renderer for an item belonging to a block entity
     */
    default void onRegisterBuiltinModelItemRenderers(BuiltinModelItemRendererContext context) {
        this.onRegisterBuiltinModelItemRenderer(context);
    }

    /**
     * @param context register additional renders to run after stack count and durability have been drawn for an item stack
     */
    default void onRegisterItemDecorations(ItemDecorationContext context) {

    }

    /**
     * @param context adds a listener to the client resource manager to reload at the end of all resources
     */
    default void onRegisterClientReloadListeners(ClientReloadListenersContext context) {

    }

    /**
     * @param context register additional {@link RenderLayer}s for a living entity
     */
    default void onRegisterLivingEntityRenderLayers(LivingEntityRenderLayersContext context) {

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
     * register a screen for a menu type
     */
    @FunctionalInterface
    interface MenuScreensContext {

        /**
         * register a factory for a {@link MenuType}
         * implementation is the same on Fabric and Forge, as both use and accesswidener/accesstransformer, which is not applied in common though
         *
         * @param menuType the menu type
         * @param factory  the factory to create a screen from when the menu is opened on the server
         * @param <M>      type of menu
         * @param <U>      type of screen
         */
        <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(MenuType<? extends M> menuType, ModScreenConstructor<M, U> factory);
    }

    /**
     * stitch a custom sprite onto an atlas
     */
    @FunctionalInterface
    interface AtlasSpritesContext {

        /**
         * convenient overload for directly registering a material
         *
         * @param material a texture material
         */
        default void registerMaterial(Material material) {
            this.registerAtlasSprite(material.atlasLocation(), material.texture());
        }

        /**
         * registers a sprite for being stitched onto an atlas
         *
         * @param atlasId the atlas to register to, since 1.14 there are multiples
         * @param spriteId the sprite to register
         */
        void registerAtlasSprite(ResourceLocation atlasId, ResourceLocation spriteId);
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
     * context for modifying baked models right after they've been loaded
     *
     * @param modelManager      the model manager
     * @param models            map of all baked models, useful to add or replace models
     * @param modelBakery       the bakery
     *
     * @deprecated              replaced with {@link DynamicModelBakingContext} in {@link #onRegisterModelBakingCompletedListeners}
     */
    @Deprecated(forRemoval = true)
    record LoadModelsContext(ModelManager modelManager, Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery) {

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
    interface ModelBakingCompletedListenersContext {

        /**
         * register a reload listener
         *
         * @param action    action that runs everytime baked models are reloaded
         */
        void registerReloadListener(Consumer<DynamicModelBakingContext> action);
    }

    /**
     * register model predicates for custom item models
     */
    interface ItemModelPropertiesContext {

        /**
         * register a predicate for all items
         *
         * @param name          predicate name
         * @param function      handler for this predicate
         */
        void register(ResourceLocation name, ClampedItemPropertyFunction function);

        /**
         * register a predicate for an <code>item</code>
         *
         * @param item          the item
         * @param name          predicate name
         * @param function      handler for this predicate
         */
        void registerItem(Item item, ResourceLocation name, ClampedItemPropertyFunction function);
    }

    /**
     * register a custom inventory renderer for an item belonging to a block entity
     */
    @FunctionalInterface
    interface BuiltinModelItemRendererContext {

        /**
         * register a <code>renderer</code> for an <code>item</code>
         *
         * @param item      the item to register for
         * @param renderer  dynamic implementation of {@link net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer}
         */
        void register(ItemLike item, DynamicBuiltinModelItemRenderer renderer);
    }

    /**
     * register additional renders to run after stack count and durability have been drawn for an item stack
     */
    @FunctionalInterface
    interface ItemDecorationContext {

        /**
         * register a {@link DynamicItemDecorator} for an <code>item</code>
         *
         * @param item              the item to draw for
         * @param decorator         renderer implementation
         */
        void register(ItemLike item, DynamicItemDecorator decorator);
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
         * @param <T>               entity type
         */
        <T extends LivingEntity> void registerRenderLayer(EntityType<? extends T> entityType, BiFunction<RenderLayerParent<T, ? extends EntityModel<T>>, EntityRendererProvider.Context, RenderLayer<T, ? extends EntityModel<T>>> factory);
    }
}
