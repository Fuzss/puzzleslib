package fuzs.puzzleslib.impl.client.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.mixin.client.accessor.ItemForgeAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
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
    private final Set<ContentRegistrationFlags> contentRegistrations;
    /**
     * actions to run each time after baked models have been reloaded
     */
    private final List<ClientModConstructor.DynamicModelBakingContext> modelBakingListeners = Lists.newArrayList();
    /**
     * custom built-in item model renderers to reload after each resource reload
     */
    private final List<ResourceManagerReloadListener> dynamicBuiltinModelItemRenderers = Lists.newArrayList();
    private final Multimap<ResourceLocation, CreativeModeTab.DisplayItemsGenerator> creativeModeTabBuildListeners = HashMultimap.create();

    /**
     * only calls {@link ModConstructor#onConstructMod()}, everything else is done via events later
     *
     * @param modId         the mod id
     * @param constructor   mod base class
     */
    private ForgeClientModConstructor(ClientModConstructor constructor, String modId, ContentRegistrationFlags... contentRegistrations) {
        this.modId = modId;
        this.constructor = constructor;
        this.contentRegistrations = ImmutableSet.copyOf(contentRegistrations);
        constructor.onConstructMod();
    }

    @SuppressWarnings("removal")
    @SubscribeEvent
    public void onClientSetup(final FMLClientSetupEvent evt) {
        this.constructor.onClientSetup(evt::enqueueWork);
        this.constructor.onRegisterSearchTrees(this.getSearchRegistryContext());
        // we store all those listeners in a list once, Forge is a bit odd here with firing the related model events on the mod event bus;
        // that bus should only really be used for registering stuff once during game initialization, but models (resources in general) can reload plenty of times while the game is running
        this.constructor.onRegisterModelBakingListeners(this.modelBakingListeners::add);
        this.constructor.onRegisterItemModelProperties(this.getItemPropertiesContext());
        this.constructor.onRegisterBuiltinModelItemRenderers(this.getBuiltinModelItemRendererContext());
        this.constructor.onRegisterBlockRenderTypes((renderType, object, objects) -> {
            Objects.requireNonNull(renderType, "render type is null");
            Objects.requireNonNull(object, "block is null");
            ItemBlockRenderTypes.setRenderLayer(object, renderType);
            Objects.requireNonNull(objects, "blocks is null");
            for (Block block : objects) {
                Objects.requireNonNull(block, "block is null");
                ItemBlockRenderTypes.setRenderLayer(block, renderType);
            }
        });
        this.constructor.onRegisterFluidRenderTypes((renderType, object, objects) -> {
            Objects.requireNonNull(renderType, "render type is null");
            Objects.requireNonNull(object, "fluid is null");
            ItemBlockRenderTypes.setRenderLayer(object, renderType);
            Objects.requireNonNull(objects, "fluids is null");
            for (Fluid fluid : objects) {
                Objects.requireNonNull(fluid, "fluid is null");
                ItemBlockRenderTypes.setRenderLayer(fluid, renderType);
            }
        });
        this.constructor.onBuildCreativeModeTabContents(new ClientModConstructor.BuildCreativeModeTabContentsContext() {

            @Override
            public void registerBuildListener(ResourceLocation identifier, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
                Objects.requireNonNull(identifier, "identifier is null");
                Objects.requireNonNull(displayItemsGenerator, "display items generator is null");
                ForgeClientModConstructor.this.creativeModeTabBuildListeners.put(identifier, displayItemsGenerator);
            }

            @Override
            public void registerBuildListener(CreativeModeTab creativeModeTab, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
                Objects.requireNonNull(creativeModeTab, "creative mode tab is null");
                ResourceLocation identifier = CreativeModeTabRegistry.getName(creativeModeTab);
                Objects.requireNonNull(identifier, "identifier is null");
                Objects.requireNonNull(displayItemsGenerator, "display items generator is null");
                ForgeClientModConstructor.this.creativeModeTabBuildListeners.put(identifier, displayItemsGenerator);
            }
        });
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
            public void registerGlobalProperty(ResourceLocation identifier, ClampedItemPropertyFunction function) {
                Objects.requireNonNull(identifier, "property name is null");
                Objects.requireNonNull(function, "property function is null");
                ItemProperties.registerGeneric(identifier, function);
            }

            @Override
            public void registerItemProperty(ResourceLocation identifier, ClampedItemPropertyFunction function, ItemLike object, ItemLike... objects) {
                Objects.requireNonNull(identifier, "property name is null");
                Objects.requireNonNull(function, "property function is null");
                Objects.requireNonNull(object, "item is null");
                ItemProperties.register(object.asItem(), identifier, function);
                Objects.requireNonNull(objects, "items is null");
                for (ItemLike item : objects) {
                    Objects.requireNonNull(item, "item is null");
                    ItemProperties.register(item.asItem(), identifier, function);
                }
            }
        };
    }

    private ClientModConstructor.BuiltinModelItemRendererContext getBuiltinModelItemRendererContext() {
        return (renderer, object, objects) -> {
            // copied from Forge, seems to break data gen otherwise
            if (FMLLoader.getLaunchHandler().isData()) return;
            Objects.requireNonNull(renderer, "renderer is null");
            // store this to enable listening to resource reloads
            this.dynamicBuiltinModelItemRenderers.add(renderer);
            // this solution is very dangerous as it relies on internal stuff in Forge
            // but there is no other way for multi-loader and without making this a huge inconvenience so ¯\_(ツ)_/¯
            IClientItemExtensions clientItemExtension = new IClientItemExtensions() {

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
            Objects.requireNonNull(object, "item is null");
            setClientItemExtensions(object, clientItemExtension);
            Objects.requireNonNull(objects, "items is null");
            for (ItemLike item : objects) {
                Objects.requireNonNull(item, "item is null");
                setClientItemExtensions(item, clientItemExtension);
            }
        };
    }

    private static void setClientItemExtensions(ItemLike item, IClientItemExtensions clientItemExtensions) {
        Object currentClientItemExtension = ((ItemForgeAccessor) item.asItem()).puzzleslib$getRenderProperties();
        ((ItemForgeAccessor) item.asItem()).puzzleslib$setRenderProperties(currentClientItemExtension != null ? new ForwardingClientItemExtensions((IClientItemExtensions) currentClientItemExtension) {

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return clientItemExtensions.getCustomRenderer();
            }
        } : clientItemExtensions);
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
            public <T extends ParticleOptions> void registerParticleFactory(ParticleType<T> type, ParticleEngine.SpriteParticleRegistration<T> factory) {
                Objects.requireNonNull(type, "particle type is null");
                Objects.requireNonNull(factory, "particle provider factory is null");
                evt.register(type, factory);
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
    public void onModifyBakingResult(final ModelEvent.ModifyBakingResult evt) {
        for (ClientModConstructor.DynamicModelBakingContext listener : this.modelBakingListeners) {
            if (!(listener instanceof ClientModConstructor.ModifyBakingResult modifyBakingResult)) return;
            try {
                modifyBakingResult.onModifyBakingResult(evt.getModels(), evt.getModelBakery());
            } catch (Exception e) {
                PuzzlesLib.LOGGER.error("Unable to execute additional resource pack model processing provided by {}", this.modId, e);
            }
        }
    }

    @SubscribeEvent
    public void onBakingCompleted(final ModelEvent.BakingCompleted evt) {
        for (ClientModConstructor.DynamicModelBakingContext listener : this.modelBakingListeners) {
            if (!(listener instanceof ClientModConstructor.BakingCompleted bakingCompleted)) return;
            try {
                bakingCompleted.onBakingCompleted(evt.getModelManager(), evt.getModels(), evt.getModelBakery());
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
        this.constructor.onRegisterItemDecorations((decorator, object, objects) -> {
            Objects.requireNonNull(decorator, "item decorator is null");
            Objects.requireNonNull(object, "item is null");
            evt.register(object, decorator::renderItemDecorations);
            Objects.requireNonNull(objects, "items is null");
            for (ItemLike item : objects) {
                Objects.requireNonNull(item, "item is null");
                evt.register(item, decorator::renderItemDecorations);
            }
        });
    }

    @SubscribeEvent
    public void onRegisterEntitySpectatorShaders(final RegisterEntitySpectatorShadersEvent evt) {
        this.constructor.onRegisterEntitySpectatorShaders((shaderLocation, object, objects) -> {
            Objects.requireNonNull(shaderLocation, "shader location is null");
            Objects.requireNonNull(object, "entity type is null");
            evt.register(object, shaderLocation);
            Objects.requireNonNull(objects, "entity types is null");
            for (EntityType<?> entityType : objects) {
                Objects.requireNonNull(entityType, "entity type is null");
                evt.register(entityType, shaderLocation);
            }
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
        if (this.contentRegistrations.contains(ContentRegistrationFlags.BUILT_IN_ITEM_MODEL_RENDERERS)) {
            // always register this, the event runs before built-in model item renderers, so the list is always empty at this point
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

            @SuppressWarnings("unchecked")
            @Override
            public <E extends LivingEntity, T extends E, M extends EntityModel<T>> void registerRenderLayer(EntityType<E> entityType, BiFunction<RenderLayerParent<T, M>, EntityRendererProvider.Context, RenderLayer<T, M>> factory) {
                Objects.requireNonNull(entityType, "entity type is null");
                Objects.requireNonNull(factory, "render layer factory is null");
                if (entityType == EntityType.PLAYER) {
                    evt.getSkins().stream()
                            .map(evt::getSkin)
                            .filter(Objects::nonNull)
                            .map(entityRenderer -> ((LivingEntityRenderer<T, M>) entityRenderer))
                            .forEach(entityRenderer -> {
                                this.actuallyRegisterRenderLayerV2(entityRenderer, factory);
                            });
                } else {
                    LivingEntityRenderer<T, M> entityRenderer = (LivingEntityRenderer<T, M>) evt.getRenderer(entityType);
                    Objects.requireNonNull(entityRenderer, "entity renderer for %s is null".formatted(ForgeRegistries.ENTITY_TYPES.getKey(entityType).toString()));
                    this.actuallyRegisterRenderLayerV2(entityRenderer, factory);
                }
            }

            private <T extends LivingEntity, M extends EntityModel<T>> void actuallyRegisterRenderLayerV2(LivingEntityRenderer<T, M> entityRenderer, BiFunction<RenderLayerParent<T, M>, EntityRendererProvider.Context, RenderLayer<T, M>> factory) {
                Minecraft minecraft = Minecraft.getInstance();
                // not sure if there's a way for getting the reload manager that's actually reloading this currently, let's just hope we never need it here
                EntityRendererProvider.Context context = new EntityRendererProvider.Context(minecraft.getEntityRenderDispatcher(), minecraft.getItemRenderer(), minecraft.getBlockRenderer(), minecraft.getEntityRenderDispatcher().getItemInHandRenderer(), null, evt.getEntityModels(), minecraft.font);
                entityRenderer.addLayer(factory.apply(entityRenderer, context));
            }
        });
    }

    @SubscribeEvent
    public void onRegisterKeyMappings(final RegisterKeyMappingsEvent evt) {
        this.constructor.onRegisterKeyMappings((KeyMapping... keyMappings) -> {
            Objects.requireNonNull(keyMappings, "key mappings is null");
            for (KeyMapping keyMapping : keyMappings) {
                Objects.requireNonNull(keyMapping, "key mapping is null");
                evt.register(keyMapping);
            }
        });
    }

    @SubscribeEvent
    public void onRegisterBlockColorHandlers(final RegisterColorHandlersEvent.Block evt) {
        this.constructor.onRegisterBlockColorProviders(new ClientModConstructor.ColorProvidersContext<>() {

            @Override
            public void registerColorProvider(BlockColor provider, Block object, Block... objects) {
                Objects.requireNonNull(provider, "provider is null");
                this.registerItemColorProvider(object, provider);
                Objects.requireNonNull(objects, "blocks is null");
                for (Block block : objects) {
                    this.registerItemColorProvider(block, provider);
                }
            }

            private void registerItemColorProvider(Block block, BlockColor provider) {
                Objects.requireNonNull(block, "block is null");
                evt.register(provider, block);
            }

            @Override
            public BlockColor getProviders() {
                return evt.getBlockColors()::getColor;
            }
        });
    }

    @SubscribeEvent
    public void onRegisterItemColorHandlers(final RegisterColorHandlersEvent.Item evt) {
        this.constructor.onRegisterItemColorProviders(new ClientModConstructor.ColorProvidersContext<>() {

            @Override
            public void registerColorProvider(ItemColor provider, Item object, Item... objects) {
                Objects.requireNonNull(provider, "provider is null");
                this.registerItemColorProvider(object, provider);
                Objects.requireNonNull(objects, "items is null");
                for (Item item : objects) {
                    this.registerItemColorProvider(item, provider);
                }
            }

            private void registerItemColorProvider(Item item, ItemColor provider) {
                Objects.requireNonNull(item, "item is null");
                evt.register(provider, item);
            }

            @Override
            public ItemColor getProviders() {
                return evt.getItemColors()::getColor;
            }
        });
    }

    @SubscribeEvent
    public void onCreativeModeTab$BuildContents(final CreativeModeTabEvent.BuildContents evt) {
        ResourceLocation identifier = CreativeModeTabRegistry.getName(evt.getTab());
        if (identifier == null) return;
        for (CreativeModeTab.DisplayItemsGenerator itemsGenerator : this.creativeModeTabBuildListeners.get(identifier)) {
            itemsGenerator.accept(evt.getFlags(), evt, evt.hasPermissions());
        }
    }

    /**
     * construct the mod, calling all necessary registration methods
     * we don't need the object, it's only important for being registered to the necessary events buses
     *
     * @param modId the mod id for registering events on Forge to the correct mod event bus
     * @param constructor mod base class
     */
    public static void construct(ClientModConstructor constructor, String modId, ContentRegistrationFlags... contentRegistrations) {
        if (Strings.isBlank(modId)) throw new IllegalArgumentException("modId cannot be empty");
        PuzzlesLib.LOGGER.info("Constructing client components for mod {}", modId);
        ForgeClientModConstructor forgeModConstructor = new ForgeClientModConstructor(constructor, modId, contentRegistrations);
        ModContainerHelper.findModEventBus(modId).register(forgeModConstructor);
    }
}
