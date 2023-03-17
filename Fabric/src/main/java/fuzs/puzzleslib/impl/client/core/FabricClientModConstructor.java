package fuzs.puzzleslib.impl.client.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.events.v2.EntitySpectatorShaderRegistry;
import fuzs.puzzleslib.api.client.events.v2.ItemDecoratorRegistry;
import fuzs.puzzleslib.api.client.events.v2.ModelEvents;
import fuzs.puzzleslib.api.client.events.v2.SkullRenderersRegistry;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.mixin.client.accessor.MinecraftFabricAccessor;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
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
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
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
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
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
import org.apache.logging.log4j.util.Strings;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
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
     * @param constructor the common mod main class implementation
     * @param modId       the mod id
     */
    private FabricClientModConstructor(ClientModConstructor constructor, String modId) {
        this.modId = modId;
        // only call ModConstructor::onConstructMod during object construction to be similar to Forge
        constructor.onConstructMod();
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

    private ClientModConstructor.BlockEntityRenderersContext getBlockEntityRenderersContext() {
        return new ClientModConstructor.BlockEntityRenderersContext() {

            @Override
            public <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider) {
                Objects.requireNonNull(blockEntityType, "block entity type is null");
                Objects.requireNonNull(blockEntityRendererProvider, "block entity renderer provider is null");
                BlockEntityRenderers.register(blockEntityType, blockEntityRendererProvider);
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
            public <T extends ParticleOptions> void registerParticleFactory(ParticleType<T> type, ParticleEngine.SpriteParticleRegistration<T> factory) {
                Objects.requireNonNull(type, "particle type is null");
                Objects.requireNonNull(factory, "particle provider factory is null");
                ParticleFactoryRegistry.getInstance().register(type, factory::create);
            }
        };
    }

    private void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
        Objects.requireNonNull(layerLocation, "layer location is null");
        Objects.requireNonNull(supplier, "layer supplier is null");
        EntityModelLayerRegistry.registerModelLayer(layerLocation, supplier::get);
    }

    private <T> void registerSearchTree(SearchRegistry.Key<T> searchRegistryKey, SearchRegistry.TreeBuilderSupplier<T> treeBuilder) {
        Objects.requireNonNull(searchRegistryKey, "search registry key is null");
        Objects.requireNonNull(treeBuilder, "search registry tree builder is null");
        SearchRegistry searchTreeManager = ((MinecraftFabricAccessor) Minecraft.getInstance()).puzzleslib$getSearchRegistry();
        Objects.requireNonNull(searchTreeManager, "search tree manager is null");
        searchTreeManager.register(searchRegistryKey, treeBuilder);
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

            @SuppressWarnings("unchecked")
            @Override
            public <E extends LivingEntity, T extends E, M extends EntityModel<T>> void registerRenderLayer(EntityType<E> entityType, BiFunction<RenderLayerParent<T, M>, EntityRendererProvider.Context, RenderLayer<T, M>> factory) {
                Objects.requireNonNull(entityType, "entity type is null");
                Objects.requireNonNull(factory, "render layer factory is null");
                LivingEntityFeatureRendererRegistrationCallback.EVENT.register((EntityType<? extends LivingEntity> entityType1, LivingEntityRenderer<?, ?> entityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) -> {
                    if (entityType == entityType1) registrationHelper.register(factory.apply((RenderLayerParent<T, M>) entityRenderer, context));
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
    public static void construct(ClientModConstructor constructor, String modId, ContentRegistrationFlags... contentRegistrations) {
        if (Strings.isBlank(modId)) throw new IllegalArgumentException("modId cannot be empty");
        PuzzlesLib.LOGGER.info("Constructing client components for mod {}", modId);
        FabricClientModConstructor fabricClientModConstructor = new FabricClientModConstructor(constructor, modId);
        // everything after this is done on Forge using events called by the mod event bus
        // this is done since Forge works with loading stages, Fabric doesn't have those stages, so everything is called immediately
        constructor.onClientSetup(Runnable::run);
        constructor.onRegisterEntityRenderers(fabricClientModConstructor.getEntityRenderersContext());
        constructor.onRegisterBlockEntityRenderers(fabricClientModConstructor.getBlockEntityRenderersContext());
        constructor.onRegisterClientTooltipComponents(fabricClientModConstructor::registerClientTooltipComponent);
        constructor.onRegisterParticleProviders(fabricClientModConstructor.getParticleProvidersContext());
        constructor.onRegisterLayerDefinitions(fabricClientModConstructor::registerLayerDefinition);
        constructor.onRegisterSearchTrees(fabricClientModConstructor::registerSearchTree);
        final List<ClientModConstructor.DynamicModelBakingContext> modelBakingListeners = Lists.newArrayList();
        constructor.onRegisterModelBakingListeners(modelBakingListeners::add);
        if (!modelBakingListeners.isEmpty()) {
            ModelEvents.MODIFY_BAKING_RESULT.register((models, modelBakery) -> {
                for (ClientModConstructor.DynamicModelBakingContext listener : modelBakingListeners) {
                    if (!(listener instanceof ClientModConstructor.ModifyBakingResult modifyBakingResult)) continue;
                    try {
                        modifyBakingResult.onModifyBakingResult(models, modelBakery);
                    } catch (Exception e) {
                        PuzzlesLib.LOGGER.error("Unable to execute additional resource pack model processing provided by {}", modId, e);
                    }
                }
            });
            ModelEvents.BAKING_COMPLETED.register((modelManager, models, modelBakery) -> {
                for (ClientModConstructor.DynamicModelBakingContext listener : modelBakingListeners) {
                    if (!(listener instanceof ClientModConstructor.BakingCompleted bakingCompleted)) continue;
                    try {
                        bakingCompleted.onBakingCompleted(modelManager, models, modelBakery);
                    } catch (Exception e) {
                        PuzzlesLib.LOGGER.error("Unable to execute additional resource pack model processing provided by {}", modId, e);
                    }
                }
            });
        }
        constructor.onRegisterAdditionalModels((ResourceLocation model) -> {
            Objects.requireNonNull(model, "model location is null");
            ModelLoadingRegistry.INSTANCE.registerModelProvider((ResourceManager manager, Consumer<ResourceLocation> out) -> {
                out.accept(model);
            });
        });
        constructor.onRegisterItemModelProperties(fabricClientModConstructor.getItemPropertiesContext());
        constructor.onRegisterEntitySpectatorShaders((shaderLocation, object, objects) -> {
            Objects.requireNonNull(shaderLocation, "shader location is null");
            Objects.requireNonNull(object, "entity type is null");
            EntitySpectatorShaderRegistry.INSTANCE.register(object, shaderLocation);
            Objects.requireNonNull(objects, "entity types is null");
            for (EntityType<?> entityType : objects) {
                Objects.requireNonNull(entityType, "entity type is null");
                EntitySpectatorShaderRegistry.INSTANCE.register(entityType, shaderLocation);
            }
        });
        final List<ResourceManagerReloadListener> dynamicBuiltinModelItemRenderers = Lists.newArrayList();
        constructor.onRegisterBuiltinModelItemRenderers((renderer, object, objects) -> {
            Objects.requireNonNull(renderer, "renderer is null");
            dynamicBuiltinModelItemRenderers.add(renderer);
            Objects.requireNonNull(object, "item is null");
            BuiltinItemRendererRegistry.INSTANCE.register(object, renderer::renderByItem);
            Objects.requireNonNull(objects, "items is null");
            for (ItemLike item : objects) {
                Objects.requireNonNull(item, "item is null");
                BuiltinItemRendererRegistry.INSTANCE.register(item, renderer::renderByItem);
            }
        });
        if (!dynamicBuiltinModelItemRenderers.isEmpty()) {
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(fabricClientModConstructor.getFabricResourceReloadListener("built_in_model_item_renderers", (ResourceManagerReloadListener) (ResourceManager resourceManager) -> {
                for (ResourceManagerReloadListener listener : dynamicBuiltinModelItemRenderers) {
                    listener.onResourceManagerReload(resourceManager);
                }
            }));
        }
        constructor.onRegisterClientReloadListeners((String id, PreparableReloadListener reloadListener) -> {
            Objects.requireNonNull(id, "reload listener id is null");
            Objects.requireNonNull(reloadListener, "reload listener is null");
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(fabricClientModConstructor.getFabricResourceReloadListener(id, reloadListener));
        });
        constructor.onRegisterLivingEntityRenderLayers(fabricClientModConstructor.getLivingEntityRenderLayersContext());
        constructor.onRegisterItemDecorations((decorator, object, objects) -> {
            Objects.requireNonNull(decorator, "item decorator is null");
            Objects.requireNonNull(object, "item is null");
            ItemDecoratorRegistry.INSTANCE.register(object, decorator);
            Objects.requireNonNull(objects, "items is null");
            for (ItemLike item : objects) {
                Objects.requireNonNull(item, "item is null");
                ItemDecoratorRegistry.INSTANCE.register(item, decorator);
            }
        });
        constructor.onRegisterSkullRenderers(factory -> {
            Objects.requireNonNull(factory, "factory is null");
            SkullRenderersRegistry.INSTANCE.register(factory);
        });
        constructor.onRegisterKeyMappings((KeyMapping... keyMappings) -> {
            Objects.requireNonNull(keyMappings, "key mappings is null");
            for (KeyMapping keyMapping : keyMappings) {
                Objects.requireNonNull(keyMapping, "key mapping is null");
                KeyBindingHelper.registerKeyBinding(keyMapping);
            }
        });
        constructor.onRegisterBlockRenderTypes((renderType, object, objects) -> {
            Objects.requireNonNull(renderType, "render type is null");
            Objects.requireNonNull(object, "block is null");
            BlockRenderLayerMap.INSTANCE.putBlock(object, renderType);
            Objects.requireNonNull(objects, "blocks is null");
            for (Block block : objects) {
                Objects.requireNonNull(block, "block is null");
                BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
            }
        });
        constructor.onRegisterFluidRenderTypes((renderType, object, objects) -> {
            Objects.requireNonNull(renderType, "render type is null");
            Objects.requireNonNull(object, "fluid is null");
            BlockRenderLayerMap.INSTANCE.putFluid(object, renderType);
            Objects.requireNonNull(objects, "fluids is null");
            for (Fluid fluid : objects) {
                Objects.requireNonNull(fluid, "fluid is null");
                BlockRenderLayerMap.INSTANCE.putFluid(fluid, renderType);
            }
        });
        constructor.onRegisterBlockColorProviders(new ClientModConstructor.ColorProvidersContext<>() {

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
                ColorProviderRegistry.BLOCK.register(provider, block);
            }

            @Override
            public BlockColor getProviders() {
                return (blockState, blockAndTintGetter, blockPos, i) -> {
                    BlockColor blockColor = ColorProviderRegistry.BLOCK.get(blockState.getBlock());
                    return blockColor == null ? -1 : blockColor.getColor(blockState, blockAndTintGetter, blockPos, i);
                };
            }
        });
        constructor.onRegisterItemColorProviders(new ClientModConstructor.ColorProvidersContext<>() {

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
                ColorProviderRegistry.ITEM.register(provider, item);
            }

            @Override
            public ItemColor getProviders() {
                return (itemStack, i) -> {
                    ItemColor itemColor = ColorProviderRegistry.ITEM.get(itemStack.getItem());
                    return itemColor == null ? -1 : itemColor.getColor(itemStack, i);
                };
            }
        });
        constructor.onBuildCreativeModeTabContents(new ClientModConstructor.BuildCreativeModeTabContentsContext() {

            @Override
            public void registerBuildListener(ResourceLocation identifier, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
                Objects.requireNonNull(identifier, "identifier is null");
                Objects.requireNonNull(displayItemsGenerator, "display items generator is null");
                ItemGroupEvents.modifyEntriesEvent(identifier).register(entries -> displayItemsGenerator.accept(entries.getEnabledFeatures(), entries, entries.shouldShowOpRestrictedItems()));
            }

            @Override
            public void registerBuildListener(CreativeModeTab creativeModeTab, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
                Objects.requireNonNull(creativeModeTab, "creative mode tab is null");
                Objects.requireNonNull(displayItemsGenerator, "display items generator is null");
                ItemGroupEvents.modifyEntriesEvent(creativeModeTab).register(entries -> displayItemsGenerator.accept(entries.getEnabledFeatures(), entries, entries.shouldShowOpRestrictedItems()));
            }
        });
    }
}
