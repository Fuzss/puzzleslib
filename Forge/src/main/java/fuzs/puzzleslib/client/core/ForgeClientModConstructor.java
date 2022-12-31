package fuzs.puzzleslib.client.core;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.client.extension.WrappedClientItemExtension;
import fuzs.puzzleslib.client.init.builder.ModScreenConstructor;
import fuzs.puzzleslib.client.init.builder.ModSpriteParticleRegistration;
import fuzs.puzzleslib.client.renderer.DynamicBuiltinModelItemRenderer;
import fuzs.puzzleslib.client.renderer.entity.DynamicItemDecorator;
import fuzs.puzzleslib.client.resources.model.DynamicModelBakingContext;
import fuzs.puzzleslib.core.ModConstructor;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.mixin.client.accessor.ItemForgeAccessor;
import fuzs.puzzleslib.util.PuzzlesUtilForge;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * wrapper class for {@link ClientModConstructor} for calling all required registration methods at the correct time
 * most things need events for registering
 *
 * <p>we use this wrapper style to allow for already registered to be used within the registration methods instead of having to use suppliers
 */
public class ForgeClientModConstructor {
    /**
     * the mod id
     */
    private final String modId;
    /**
     * mod base class
     */
    private final ClientModConstructor constructor;
    /**
     * actions to run each time after baked models have been reloaded
     */
    private final List<Consumer<DynamicModelBakingContext>> modelBakingListeners = Lists.newArrayList();
    /**
     * custom built-in item model renderers to reload after each resource reload
     */
    private final List<ResourceManagerReloadListener> dynamicBuiltinModelItemRenderers = Lists.newArrayList();

    /**
     * only calls {@link ModConstructor#onConstructMod()}, everything else is done via events later
     *
     * @param modId         the mod id
     * @param constructor   mod base class
     */
    private ForgeClientModConstructor(String modId, ClientModConstructor constructor) {
        this.modId = modId;
        this.constructor = constructor;
        constructor.onConstructMod();
    }

    @SubscribeEvent
    public void onClientSetup(final FMLClientSetupEvent evt) {
        this.constructor.onClientSetup();
        this.constructor.onRegisterMenuScreens(this.getMenuScreensContext());
        this.constructor.onRegisterSearchTrees(this.getSearchRegistryContext());
        this.constructor.onRegisterModelBakingCompletedListeners(this.modelBakingListeners::add);
        this.constructor.onRegisterItemModelProperties(this.getItemPropertiesContext());
        this.constructor.onRegisterBuiltinModelItemRenderers(this.getBuiltinModelItemRendererContext());
    }

    private ClientModConstructor.MenuScreensContext getMenuScreensContext() {
        return new ClientModConstructor.MenuScreensContext() {

            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(MenuType<? extends M> menuType, ModScreenConstructor<M, U> factory) {
                Objects.requireNonNull(menuType, "menu type is null");
                Objects.requireNonNull(factory, "screen constructor is null");
                MenuScreens.register(menuType, factory::create);
            }
        };
    }

    private ClientModConstructor.SearchRegistryContext getSearchRegistryContext() {
        return new ClientModConstructor.SearchRegistryContext() {

            @Override
            public <T> void registerSearchTree(SearchRegistry.Key<T> searchRegistryKey, SearchRegistry.TreeBuilderSupplier<T> treeBuilder) {
                Objects.requireNonNull(searchRegistryKey, "search registry key is null");
                Objects.requireNonNull(treeBuilder, "search registry tree builder is null");
                SearchRegistry searchTreeManager = Minecraft.getInstance().getSearchTreeManager();
                Objects.requireNonNull(searchTreeManager, "search tree manager is null");
                searchTreeManager.register(searchRegistryKey, treeBuilder);
            }
        };
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

    @SuppressWarnings("UnstableApiUsage")
    private ClientModConstructor.BuiltinModelItemRendererContext getBuiltinModelItemRendererContext() {
        return (ItemLike item, DynamicBuiltinModelItemRenderer renderer) -> {
            Objects.requireNonNull(item, "item is null");
            Objects.requireNonNull(renderer, "renderer is null");
            // copied from Forge, supposed to break data gen otherwise
            if (FMLLoader.getLaunchHandler().isData()) return;
            // this solution is very dangerous as it relies on internal stuff in Forge
            // but there is no other way for multi-loader and without making this a huge inconvenience so ¯\_(ツ)_/¯
            final IClientItemExtensions clientItemExtension = new IClientItemExtensions() {

                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    Minecraft minecraft = Minecraft.getInstance();
                    return new BlockEntityWithoutLevelRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels()) {

                        @Override
                        public void renderByItem(ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
                            renderer.renderByItem(stack, mode, matrices, vertexConsumers, light, overlay);
                        }
                    };
                }
            };
            Object currentClientItemExtension = ((ItemForgeAccessor) item.asItem()).getRenderProperties();
            ((ItemForgeAccessor) item.asItem()).setRenderProperties(currentClientItemExtension != null ? new WrappedClientItemExtension((IClientItemExtensions) currentClientItemExtension) {

                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return clientItemExtension.getCustomRenderer();
                }
            } : clientItemExtension);
            this.dynamicBuiltinModelItemRenderers.add(renderer);
        };
    }

    @SubscribeEvent
    public void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers evt) {
        this.constructor.onRegisterEntityRenderers(new ClientModConstructor.EntityRenderersContext() {

            @Override
            public <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider) {
                Objects.requireNonNull(entityType, "entity type is null");
                Objects.requireNonNull(entityRendererProvider, "entity renderer provider is null");
                evt.registerEntityRenderer(entityType, entityRendererProvider);
            }
        });
        this.constructor.onRegisterBlockEntityRenderers(new ClientModConstructor.BlockEntityRenderersContext() {

            @Override
            public <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider) {
                Objects.requireNonNull(blockEntityType, "block entity type is null");
                Objects.requireNonNull(blockEntityRendererProvider, "block entity renderer provider is null");
                evt.registerBlockEntityRenderer(blockEntityType, blockEntityRendererProvider);
            }
        });
    }

    @SubscribeEvent
    public void onRegisterClientTooltipComponentFactories(final RegisterClientTooltipComponentFactoriesEvent evt) {
        this.constructor.onRegisterClientTooltipComponents(new ClientModConstructor.ClientTooltipComponentsContext() {

            @Override
            public <T extends TooltipComponent> void registerClientTooltipComponent(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory) {
                Objects.requireNonNull(type, "tooltip component type is null");
                Objects.requireNonNull(factory, "tooltip component factory is null");
                evt.register(type, factory);
            }
        });
    }

    @SubscribeEvent
    public void onRegisterParticleProviders(final RegisterParticleProvidersEvent evt) {
        this.constructor.onRegisterParticleProviders(new ClientModConstructor.ParticleProvidersContext() {

            @Override
            public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> type, ParticleProvider<T> provider) {
                Objects.requireNonNull(type, "particle type is null");
                Objects.requireNonNull(provider, "particle provider is null");
                evt.register(type, provider);
            }

            @Override
            public <T extends ParticleOptions> void registerParticleFactory(ParticleType<T> type, ModSpriteParticleRegistration<T> factory) {
                Objects.requireNonNull(type, "particle type is null");
                Objects.requireNonNull(factory, "particle provider factory is null");
                evt.register(type, factory::create);
            }
        });
    }

    @SubscribeEvent
    public void onTextureStitch(final TextureStitchEvent.Pre evt) {
        this.constructor.onRegisterAtlasSprites((ResourceLocation atlasId, ResourceLocation spriteId) -> {
            Objects.requireNonNull(atlasId, "atlas id is null");
            Objects.requireNonNull(spriteId, "sprite id is null");
            if (evt.getAtlas().location().equals(atlasId)) {
                evt.addSprite(spriteId);
            }
        });
    }

    @SubscribeEvent
    public void onRegisterLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions evt) {
        this.constructor.onRegisterLayerDefinitions((ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) -> {
            Objects.requireNonNull(layerLocation, "layer location is null");
            Objects.requireNonNull(supplier, "layer supplier is null");
            evt.registerLayerDefinition(layerLocation, supplier);
        });
    }

    @SubscribeEvent
    public void onBakingCompleted(final ModelEvent.BakingCompleted evt) {
        final DynamicModelBakingContext context = new DynamicModelBakingContext(evt.getModelManager(), evt.getModels(), evt.getModelBakery()) {

            @Override
            public BakedModel bakeModel(ResourceLocation model) {
                Objects.requireNonNull(model, "model location is null");
                // this will never be null, if not present returns missing model
                UnbakedModel unbakedModel = this.modelBakery.getModel(model);
                return unbakedModel.bake(this.modelBakery, this.modelBakery.getAtlasSet()::getSprite, BlockModelRotation.X0_Y0, model);
            }
        };
        for (Consumer<DynamicModelBakingContext> listener : this.modelBakingListeners) {
            try {
                listener.accept(context);
            } catch (Exception e) {
                PuzzlesLib.LOGGER.error("Unable to execute additional resource pack model processing provided by {}", this.modId, e);
            }
        }
    }

    @SubscribeEvent
    public void onRegisterAdditional(final ModelEvent.RegisterAdditional evt) {
        this.constructor.onRegisterAdditionalModels((ResourceLocation model) -> {
            Objects.requireNonNull(model, "additional model is null");
            evt.register(model);
        });
    }

    @SubscribeEvent
    public void onRegisterItemDecorations(final RegisterItemDecorationsEvent evt) {
        this.constructor.onRegisterItemDecorations((ItemLike item, DynamicItemDecorator decorator) -> {
            Objects.requireNonNull(item, "item is null");
            Objects.requireNonNull(decorator, "item decorator is null");
            evt.register(item, decorator::renderItemDecorations);
        });
    }

    @SubscribeEvent
    public void onRegisterEntitySpectatorShaders(final RegisterEntitySpectatorShadersEvent evt) {
        this.constructor.onRegisterEntitySpectatorShaders((EntityType<?> entityType, ResourceLocation shader) -> {
            Objects.requireNonNull(entityType, "entity type is null");
            Objects.requireNonNull(shader, "shader location is null");
            evt.register(entityType, shader);
        });
    }

    @SubscribeEvent
    public void onCreateSkullModels(final EntityRenderersEvent.CreateSkullModels evt) {
        this.constructor.onRegisterSkullRenderers(factory -> {
            Objects.requireNonNull(factory, "factory is null");
            factory.createSkullRenderers(evt.getEntityModelSet(), evt::registerSkullModel);
        });
    }

    @SubscribeEvent
    public void onRegisterClientReloadListeners(final RegisterClientReloadListenersEvent evt) {
        this.constructor.onRegisterClientReloadListeners((String id, PreparableReloadListener reloadListener) -> {
            Objects.requireNonNull(id, "reload listener id is null");
            Objects.requireNonNull(reloadListener, "reload listener is null");
            evt.registerReloadListener(reloadListener);
        });
        if (!this.dynamicBuiltinModelItemRenderers.isEmpty()) {
            evt.registerReloadListener((ResourceManagerReloadListener) (ResourceManager resourceManager) -> {
                for (ResourceManagerReloadListener listener : this.dynamicBuiltinModelItemRenderers) {
                    listener.onResourceManagerReload(resourceManager);
                }
            });
        }
    }

    @SubscribeEvent
    public void onAddLayers(final EntityRenderersEvent.AddLayers evt) {
        this.constructor.onRegisterLivingEntityRenderLayers(new ClientModConstructor.LivingEntityRenderLayersContext() {

            @SuppressWarnings({"unchecked", "deprecation"})
            @Override
            public <T extends LivingEntity> void registerRenderLayer(EntityType<? extends T> entityType, BiFunction<RenderLayerParent<T, ? extends EntityModel<T>>, EntityRendererProvider.Context, RenderLayer<T, ? extends EntityModel<T>>> factory) {
                Objects.requireNonNull(entityType, "entity type is null");
                Objects.requireNonNull(factory, "render layer factory is null");
                if (entityType == EntityType.PLAYER) {
                    evt.getSkins().stream()
                            .map(evt::getSkin)
                            .filter(Objects::nonNull)
                            .map(entityRenderer -> ((LivingEntityRenderer<T, EntityModel<T>>) entityRenderer))
                            .forEach(entityRenderer -> {
                                this.actuallyRegisterRenderLayer(entityRenderer, factory);
                            });
                } else {
                    LivingEntityRenderer<T, EntityModel<T>> entityRenderer = evt.getRenderer(entityType);
                    Objects.requireNonNull(entityRenderer, "entity renderer for %s is null".formatted(Registry.ENTITY_TYPE.getKey(entityType).toString()));
                    this.actuallyRegisterRenderLayer(entityRenderer, factory);
                }
            }

            @SuppressWarnings("unchecked")
            private <T extends LivingEntity> void actuallyRegisterRenderLayer(LivingEntityRenderer<T, EntityModel<T>> entityRenderer, BiFunction<RenderLayerParent<T, ? extends EntityModel<T>>, EntityRendererProvider.Context, RenderLayer<T, ? extends EntityModel<T>>> factory) {
                Minecraft minecraft = Minecraft.getInstance();
                // not sure if there's a way for getting the reload manager that's actually reloading this currently, let's just hope we never need it here
                EntityRendererProvider.Context context = new EntityRendererProvider.Context(minecraft.getEntityRenderDispatcher(), minecraft.getItemRenderer(), minecraft.getBlockRenderer(), minecraft.getEntityRenderDispatcher().getItemInHandRenderer(), null, evt.getEntityModels(), minecraft.font);
                entityRenderer.addLayer((RenderLayer<T, EntityModel<T>>) factory.apply(entityRenderer, context));
            }
        });
    }

    @SubscribeEvent
    public void onRegisterKeyMappings(final RegisterKeyMappingsEvent evt) {
        this.constructor.onRegisterKeyMappings((KeyMapping key) -> {
            Objects.requireNonNull(key, "key mapping is null");
            evt.register(key);
        });
    }

    /**
     * construct the mod, calling all necessary registration methods
     * we don't need the object, it's only important for being registered to the necessary events buses
     *
     * @param modId the mod id for registering events on Forge to the correct mod event bus
     * @param constructor mod base class
     */
    public static void construct(String modId, ClientModConstructor constructor) {
        if (Strings.isBlank(modId)) throw new IllegalArgumentException("modId cannot be empty");
        PuzzlesLib.LOGGER.info("Constructing client components for mod {}", modId);
        ForgeClientModConstructor forgeModConstructor = new ForgeClientModConstructor(modId, constructor);
        PuzzlesUtilForge.findModEventBus(modId).register(forgeModConstructor);
    }
}
