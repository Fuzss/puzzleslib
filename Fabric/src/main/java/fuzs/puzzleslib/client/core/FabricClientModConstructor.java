package fuzs.puzzleslib.client.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fuzs.puzzleslib.api.client.renderer.ItemDecoratorRegistry;
import fuzs.puzzleslib.client.init.builder.ModScreenConstructor;
import fuzs.puzzleslib.client.init.builder.ModSpriteParticleRegistration;
import fuzs.puzzleslib.client.renderer.DynamicBuiltinModelItemRenderer;
import fuzs.puzzleslib.client.renderer.entity.DynamicItemDecorator;
import fuzs.puzzleslib.client.resources.model.DynamicModelBakingContext;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.resources.model.ModelManagerExtension;
import fuzs.puzzleslib.mixin.client.accessor.MinecraftAccessor;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
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
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
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
import org.apache.logging.log4j.util.Strings;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * wrapper class for {@link ClientModConstructor} for calling all required registration methods at the correct time,
 * which means everything is called immediately on Fabric (but in the correct order)
 *
 * <p>we use this wrapper style to allow for already registered to be used within the registration methods instead of having to use suppliers
 * (this doesn't really matter on Fabric)
 */
public class FabricClientModConstructor {
    /**
     * the mod id
     */
    private final String modId;
    /**
     * actions to run each time after baked models have been reloaded
     */
    private final List<Consumer<DynamicModelBakingContext>> modelBakingListeners = Lists.newArrayList();

    /**
     * @param modId         the mod id
     * @param constructor   the common mod main class implementation
     */
    private FabricClientModConstructor(String modId, ClientModConstructor constructor) {
        this.modId = modId;
        // only call ModConstructor::onConstructMod during object construction to be similar to Forge
        constructor.onConstructMod();
    }

    private ClientModConstructor.BlockEntityRenderersContext getBlockEntityRenderersContext() {
        return new ClientModConstructor.BlockEntityRenderersContext() {

            @Override
            public <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider) {
                Objects.requireNonNull(blockEntityType, "block entity type is null");
                Objects.requireNonNull(blockEntityRendererProvider, "block entity renderer provider is null");
                BlockEntityRendererRegistry.register(blockEntityType, blockEntityRendererProvider);
            }
        };
    }

    private ClientModConstructor.EntityRenderersContext getEntityRenderersContext() {
        return new ClientModConstructor.EntityRenderersContext() {

            @Override
            public <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider) {
                Objects.requireNonNull(entityType, "entity type is null");
                Objects.requireNonNull(entityRendererProvider, "entity renderer provider is null");
                EntityRendererRegistry.register(entityType, entityRendererProvider);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private <T extends TooltipComponent> void registerClientTooltipComponent(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory) {
        Objects.requireNonNull(type, "tooltip component type is null");
        Objects.requireNonNull(factory, "tooltip component factory is null");
        TooltipComponentCallback.EVENT.register((TooltipComponent data) -> {
            if (data.getClass() == type) return factory.apply((T) data);
            return null;
        });
    }

    private ClientModConstructor.ParticleProvidersContext getParticleProvidersContext() {
        return new ClientModConstructor.ParticleProvidersContext() {

            @Override
            public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> type, ParticleProvider<T> provider) {
                Objects.requireNonNull(type, "particle type is null");
                Objects.requireNonNull(provider, "particle provider is null");
                ParticleFactoryRegistry.getInstance().register(type, provider);
            }

            @Override
            public <T extends ParticleOptions> void registerParticleFactory(ParticleType<T> type, ModSpriteParticleRegistration<T> factory) {
                Objects.requireNonNull(type, "particle type is null");
                Objects.requireNonNull(factory, "particle provider factory is null");
                ParticleFactoryRegistry.getInstance().register(type, factory::create);
            }
        };
    }

    private <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(MenuType<? extends M> menuType, ModScreenConstructor<M, U> factory) {
        Objects.requireNonNull(menuType, "menu type is null");
        Objects.requireNonNull(factory, "screen constructor is null");
        MenuScreens.register(menuType, factory::create);
    }

    private void registerAtlasSprite(ResourceLocation atlasId, ResourceLocation spriteId) {
        Objects.requireNonNull(atlasId, "atlas id is null");
        Objects.requireNonNull(spriteId, "sprite id is null");
        ClientSpriteRegistryCallback.event(atlasId).register((TextureAtlas atlasTexture, ClientSpriteRegistryCallback.Registry registry) -> {
            registry.register(spriteId);
        });
    }

    private void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
        Objects.requireNonNull(layerLocation, "layer location is null");
        Objects.requireNonNull(supplier, "layer supplier is null");
        EntityModelLayerRegistry.registerModelLayer(layerLocation, supplier::get);
    }

    private <T> void registerSearchTree(SearchRegistry.Key<T> searchRegistryKey, SearchRegistry.TreeBuilderSupplier<T> treeBuilder) {
        Objects.requireNonNull(searchRegistryKey, "search registry key is null");
        Objects.requireNonNull(treeBuilder, "search registry tree builder is null");
        SearchRegistry searchTreeManager = ((MinecraftAccessor) Minecraft.getInstance()).getSearchRegistry();
        Objects.requireNonNull(searchTreeManager, "search tree manager is null");
        searchTreeManager.register(searchRegistryKey, treeBuilder);
    }

    private ClientModConstructor.ItemModelPropertiesContext getItemPropertiesContext() {
        return new ClientModConstructor.ItemModelPropertiesContext() {

            @Override
            public void register(ResourceLocation name, ClampedItemPropertyFunction function) {
                Objects.requireNonNull(name, "property name is null");
                Objects.requireNonNull(function, "property function is null");
                ItemProperties.registerGeneric(name, function);
            }

            @Override
            public void registerItem(Item item, ResourceLocation name, ClampedItemPropertyFunction function) {
                Objects.requireNonNull(item, "item is null");
                Objects.requireNonNull(name, "property name is null");
                Objects.requireNonNull(function, "property function is null");
                ItemProperties.register(item, name, function);
            }
        };
    }

    private PreparableReloadListener getBakingCompletedListener() {
        return (PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) -> {
            return preparationBarrier.wait(Unit.INSTANCE).thenRunAsync(() -> {
                ModelManager modelManager = Minecraft.getInstance().getModelManager();
                final DynamicModelBakingContext context = new DynamicModelBakingContext(modelManager, ((ModelManagerExtension) modelManager).puzzleslib_getBakedRegistry(), ((ModelManagerExtension) modelManager).puzzleslib_getModelBakery()) {

                    @Override
                    public BakedModel bakeModel(ResourceLocation model) {
                        Objects.requireNonNull(model, "model location is null");
                        return BakedModelManagerHelper.getModel(this.modelManager, model);
                    }
                };
                for (Consumer<DynamicModelBakingContext> listener : this.modelBakingListeners) {
                    try {
                        listener.accept(context);
                    } catch (Exception e) {
                        PuzzlesLib.LOGGER.error("Unable to execute additional resource pack model processing provided by {}", this.modId, e);
                    }
                }
            }, executor2);
        };
    }

    private IdentifiableResourceReloadListener getFabricResourceReloadListener(String id, PreparableReloadListener reloadListener, ResourceLocation... dependencies) {
        final Collection<ResourceLocation> fabricDependencies = ImmutableList.copyOf(dependencies);
        return new IdentifiableResourceReloadListener() {

            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation(FabricClientModConstructor.this.modId, id);
            }

            @Override
            public Collection<ResourceLocation> getFabricDependencies() {
                return fabricDependencies;
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
                return reloadListener.reload(preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2);
            }
        };
    }

    private ClientModConstructor.LivingEntityRenderLayersContext getLivingEntityRenderLayersContext() {
        return new ClientModConstructor.LivingEntityRenderLayersContext() {

            @Override
            public <T extends LivingEntity> void registerRenderLayer(EntityType<? extends T> entityType, Function<EntityModelSet, RenderLayer<T, ? extends EntityModel<T>>> factory) {
                Objects.requireNonNull(entityType, "entity type is null");
                Objects.requireNonNull(factory, "render layer factory is null");
                LivingEntityFeatureRendererRegistrationCallback.EVENT.register((EntityType<? extends LivingEntity> entityType1, LivingEntityRenderer<?, ?> entityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) -> {
                    if (entityType == entityType1) registrationHelper.register(factory.apply(context.getModelSet()));
                });
            }
        };
    }

    /**
     * construct the mod, calling all necessary registration methods (we don't need the object, it's only useful on Forge)
     *
     * @param modId         the mod id for registering events on Forge to the correct mod event bus
     * @param constructor   mod base class
     */
    public static void construct(String modId, ClientModConstructor constructor) {
        if (Strings.isBlank(modId)) throw new IllegalArgumentException("modId cannot be empty");
        PuzzlesLib.LOGGER.info("Constructing client components for mod {}", modId);
        FabricClientModConstructor fabricClientModConstructor = new FabricClientModConstructor(modId, constructor);
        // everything after this is done on Forge using events called by the mod event bus
        // this is done since Forge works with loading stages, Fabric doesn't have those stages, so everything is called immediately
        constructor.onClientSetup();
        constructor.onRegisterEntityRenderers(fabricClientModConstructor.getEntityRenderersContext());
        constructor.onRegisterBlockEntityRenderers(fabricClientModConstructor.getBlockEntityRenderersContext());
        constructor.onRegisterClientTooltipComponents(fabricClientModConstructor::registerClientTooltipComponent);
        constructor.onRegisterParticleProviders(fabricClientModConstructor.getParticleProvidersContext());
        constructor.onRegisterMenuScreens(fabricClientModConstructor::registerMenuScreen);
        constructor.onRegisterAtlasSprites(fabricClientModConstructor::registerAtlasSprite);
        constructor.onRegisterLayerDefinitions(fabricClientModConstructor::registerLayerDefinition);
        constructor.onRegisterSearchTrees(fabricClientModConstructor::registerSearchTree);
        constructor.onRegisterModelBakingCompletedListeners(fabricClientModConstructor.modelBakingListeners::add);
        if (!fabricClientModConstructor.modelBakingListeners.isEmpty()) {
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(fabricClientModConstructor.getFabricResourceReloadListener("model_baking_completed_listeners", fabricClientModConstructor.getBakingCompletedListener(), ResourceReloadListenerKeys.MODELS));
        }
        constructor.onRegisterAdditionalModels((ResourceLocation model) -> {
            Objects.requireNonNull(model, "model location is null");
            ModelLoadingRegistry.INSTANCE.registerModelProvider((ResourceManager manager, Consumer<ResourceLocation> out) -> {
                out.accept(model);
            });
        });
        constructor.onRegisterItemModelProperties(fabricClientModConstructor.getItemPropertiesContext());
        constructor.onRegisterBuiltinModelItemRenderers((ItemLike item, DynamicBuiltinModelItemRenderer renderer) -> {
            Objects.requireNonNull(item, "item is null");
            Objects.requireNonNull(renderer, "renderer is null");
            BuiltinItemRendererRegistry.INSTANCE.register(item, renderer::renderByItem);
        });
        constructor.onRegisterClientReloadListeners((String id, PreparableReloadListener reloadListener) -> {
            Objects.requireNonNull(id, "reload listener id is null");
            Objects.requireNonNull(reloadListener, "reload listener is null");
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(fabricClientModConstructor.getFabricResourceReloadListener(id, reloadListener));
        });
        constructor.onRegisterLivingEntityRenderLayers(fabricClientModConstructor.getLivingEntityRenderLayersContext());
        constructor.onRegisterItemDecorations((ItemLike item, DynamicItemDecorator decorator) -> {
            Objects.requireNonNull(item, "item is null");
            Objects.requireNonNull(decorator, "item decorator is null");
            ItemDecoratorRegistry.INSTANCE.register(item, decorator);
        });
    }
}
