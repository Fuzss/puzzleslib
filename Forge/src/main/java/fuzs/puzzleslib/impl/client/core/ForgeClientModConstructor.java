package fuzs.puzzleslib.impl.client.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.core.v1.context.DynamicBakingCompletedContext;
import fuzs.puzzleslib.api.client.core.v1.context.DynamicModifyBakingResultContext;
import fuzs.puzzleslib.api.client.init.v1.DynamicBuiltinItemRenderer;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.core.context.*;
import fuzs.puzzleslib.mixin.client.accessor.ItemForgeAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ForgeClientModConstructor {
    private final String modId;
    private final ClientModConstructor constructor;
    private final Set<ContentRegistrationFlags> contentRegistrations;
    private final List<ResourceManagerReloadListener> dynamicBuiltinModelItemRenderers = Lists.newArrayList();

    private ForgeClientModConstructor(ClientModConstructor constructor, String modId, ContentRegistrationFlags... contentRegistrations) {
        this.modId = modId;
        this.constructor = constructor;
        this.contentRegistrations = ImmutableSet.copyOf(contentRegistrations);
        constructor.onConstructMod();
    }

    @SubscribeEvent
    public void onClientSetup(final FMLClientSetupEvent evt) {
        this.constructor.onClientSetup(evt::enqueueWork);
        this.constructor.onRegisterSearchTrees(new SearchRegistryContextForgeImpl());
        this.constructor.onRegisterItemModelProperties(new ItemModelPropertiesContextForgeImpl());
        this.constructor.onRegisterBuiltinModelItemRenderers(this.getBuiltinModelItemRendererContext());
        this.constructor.onRegisterBlockRenderTypes(new BlockRenderTypesContextForgeImpl());
        this.constructor.onRegisterFluidRenderTypes(new FluidRenderTypesContextForgeImpl());
    }

    private BuiltinModelItemRendererContext getBuiltinModelItemRendererContext() {
        return new BuiltinModelItemRendererContext() {

            @Override
            public void registerItemRenderer(DynamicBuiltinItemRenderer renderer, ItemLike... items) {
                // copied from Forge, seems to break data gen otherwise
                if (FMLLoader.getLaunchHandler().isData()) return;
                Objects.requireNonNull(renderer, "renderer is null");
                // store this to enable listening to resource reloads
                ForgeClientModConstructor.this.dynamicBuiltinModelItemRenderers.add(renderer);
                // this solution is very dangerous as it relies on internal stuff in Forge
                // but there is no other way for multi-loader and without making this a huge inconvenience so ¯\_(ツ)_/¯
                IClientItemExtensions clientItemExtension = new IClientItemExtensions() {

                    @Override
                    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                        Minecraft minecraft = Minecraft.getInstance();
                        return new BlockEntityWithoutLevelRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels()) {

                            @Override
                            public void renderByItem(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
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
        this.constructor.onRegisterEntityRenderers(new EntityRenderersContextForgeImpl(evt::registerEntityRenderer));
        this.constructor.onRegisterBlockEntityRenderers(new BlockEntityRenderersContextForgeImpl(evt::registerBlockEntityRenderer));
    }

    @SubscribeEvent
    public void onRegisterClientTooltipComponentFactories(final RegisterClientTooltipComponentFactoriesEvent evt) {
        this.constructor.onRegisterClientTooltipComponents(new ClientTooltipComponentsContextForgeImpl(evt::register));
    }

    @SubscribeEvent
    public void onRegisterParticleProviders(final RegisterParticleProvidersEvent evt) {
        this.constructor.onRegisterParticleProviders(new ParticleProvidersContextForgeImpl(evt));
    }

    @SubscribeEvent
    public void onRegisterLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions evt) {
        this.constructor.onRegisterLayerDefinitions(new LayerDefinitionsContextForgeImpl(evt::registerLayerDefinition));
    }

    @SubscribeEvent
    public void onModifyBakingResult(final ModelEvent.ModifyBakingResult evt) {
        try {
            this.constructor.onModifyBakingResult(new DynamicModifyBakingResultContext(evt.getModels(), evt.getModelBakery()));
        } catch (Exception e) {
            PuzzlesLib.LOGGER.error("Unable to execute additional resource pack model processing during modify baking result phase provided by {}", this.modId, e);
        }
    }

    @SubscribeEvent
    public void onBakingCompleted(final ModelEvent.BakingCompleted evt) {
        try {
            this.constructor.onBakingCompleted(new DynamicBakingCompletedContext(evt.getModelManager(), evt.getModels(), evt.getModelBakery()) {

                @SuppressWarnings("resource")
                @Override
                public BakedModel getModel(ResourceLocation identifier) {
                    return this.modelManager().getModel(identifier);
                }
            });
        } catch (Exception e) {
            PuzzlesLib.LOGGER.error("Unable to execute additional resource pack model processing during baking completed phase provided by {}", this.modId, e);
        }
    }

    @SubscribeEvent
    public void onRegisterAdditional(final ModelEvent.RegisterAdditional evt) {
        this.constructor.onRegisterAdditionalModels(new AdditionalModelsContextForgeImpl(evt::register));
    }

    @SubscribeEvent
    public void onRegisterItemDecorations(final RegisterItemDecorationsEvent evt) {
        this.constructor.onRegisterItemDecorations(new ItemDecorationContextForgeImpl(evt::register));
    }

    @SubscribeEvent
    public void onRegisterEntitySpectatorShaders(final RegisterEntitySpectatorShadersEvent evt) {
        this.constructor.onRegisterEntitySpectatorShaders(new EntitySpectatorShaderContextForgeImpl(evt::register));
    }

    @SubscribeEvent
    public void onCreateSkullModels(final EntityRenderersEvent.CreateSkullModels evt) {
        this.constructor.onRegisterSkullRenderers(new SkullRenderersContextForgeImpl(evt.getEntityModelSet(), evt::registerSkullModel));
    }

    @SubscribeEvent
    public void onRegisterClientReloadListeners(final RegisterClientReloadListenersEvent evt) {
        this.constructor.onRegisterResourcePackReloadListeners((String id, PreparableReloadListener reloadListener) -> {
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
        this.constructor.onRegisterLivingEntityRenderLayers(new LivingEntityRenderLayersContextForgeImpl(evt));
    }

    @SubscribeEvent
    public void onRegisterKeyMappings(final RegisterKeyMappingsEvent evt) {
        this.constructor.onRegisterKeyMappings(new KeyMappingsContextForgeImpl(evt::register));
    }

    @SubscribeEvent
    public void onRegisterBlockColorHandlers(final RegisterColorHandlersEvent.Block evt) {
        this.constructor.onRegisterBlockColorProviders(new BlockColorProvidersContextForgeImpl(evt::register, evt.getBlockColors()));
    }

    @SubscribeEvent
    public void onRegisterItemColorHandlers(final RegisterColorHandlersEvent.Item evt) {
        this.constructor.onRegisterItemColorProviders(new ItemColorProvidersContextForgeImpl(evt::register, evt.getItemColors()));
    }

    @SubscribeEvent
    public void onCreativeModeTab$BuildContents(final CreativeModeTabEvent.BuildContents evt) {
        ResourceLocation identifier = CreativeModeTabRegistry.getName(evt.getTab());
        if (identifier != null) {
            this.constructor.onBuildCreativeModeTabContents(new BuildCreativeModeTabContentsContextForgeImpl(identifier, evt.getParameters(), evt));
        }
    }

    @SubscribeEvent
    public void onAddPackFinders(final AddPackFindersEvent evt) {
        if (evt.getPackType() == PackType.CLIENT_RESOURCES) {
            this.constructor.onAddResourcePackFinders(new ResourcePackSourcesContextForgeImpl(evt::addRepositorySource));
        }
    }

    public static void construct(ClientModConstructor constructor, String modId, ContentRegistrationFlags... contentRegistrations) {
        ForgeClientModConstructor forgeModConstructor = new ForgeClientModConstructor(constructor, modId, contentRegistrations);
        ModContainerHelper.findModEventBus(modId).ifPresent(eventBus -> {
            eventBus.register(forgeModConstructor);
        });
    }
}
