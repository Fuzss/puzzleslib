package fuzs.puzzleslib.neoforge.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.client.core.context.*;
import fuzs.puzzleslib.neoforge.impl.core.context.AbstractNeoForgeContext;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.AddPackFindersEvent;

public final class NeoForgeClientModConstructor implements ModConstructorImpl<ClientModConstructor> {

    @Override
    public void construct(String modId, ClientModConstructor modConstructor) {
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            SkullRenderersContextNeoForgeImpl[] skullRenderersContext = new SkullRenderersContextNeoForgeImpl[1];
            ItemModelsContextNeoForgeImpl[] itemModelsContext = new ItemModelsContextNeoForgeImpl[1];
            modConstructor.onConstructMod();
            eventBus.addListener((final FMLClientSetupEvent event) -> {
                event.enqueueWork(() -> {
                    modConstructor.onClientSetup();
                    AbstractNeoForgeContext.computeIfAbsent(skullRenderersContext,
                            SkullRenderersContextNeoForgeImpl::new,
                            modConstructor::onRegisterSkullRenderers).registerForEvent(event);
                });
            });
            // let this run after other mods, some of our mods are likely going to reference what other mods have registered
            eventBus.addListener(EventPriority.LOW, (final FMLClientSetupEvent event) -> {
                // need to run this deferred as the underlying registries do not use concurrent maps
                event.enqueueWork(() -> {
                    modConstructor.onRegisterBlockRenderTypes(new BlockRenderTypesContextNeoForgeImpl());
                    modConstructor.onRegisterFluidRenderTypes(new FluidRenderTypesContextNeoForgeImpl());
                });
            });
            eventBus.addListener((final RegisterItemModelsEvent event) -> {
                AbstractNeoForgeContext.computeIfAbsent(itemModelsContext,
                        ItemModelsContextNeoForgeImpl::new,
                        modConstructor::onRegisterItemModels).registerForEvent(event);
            });
            eventBus.addListener((final RegisterSpecialModelRendererEvent event) -> {
                AbstractNeoForgeContext.computeIfAbsent(itemModelsContext,
                        ItemModelsContextNeoForgeImpl::new,
                        modConstructor::onRegisterItemModels).registerForEvent(event);
            });
            eventBus.addListener((final RegisterColorHandlersEvent.ItemTintSources event) -> {
                AbstractNeoForgeContext.computeIfAbsent(itemModelsContext,
                        ItemModelsContextNeoForgeImpl::new,
                        modConstructor::onRegisterItemModels).registerForEvent(event);
            });
            eventBus.addListener((final RegisterSelectItemModelPropertyEvent event) -> {
                AbstractNeoForgeContext.computeIfAbsent(itemModelsContext,
                        ItemModelsContextNeoForgeImpl::new,
                        modConstructor::onRegisterItemModels).registerForEvent(event);
            });
            eventBus.addListener((final RegisterConditionalItemModelPropertyEvent event) -> {
                AbstractNeoForgeContext.computeIfAbsent(itemModelsContext,
                        ItemModelsContextNeoForgeImpl::new,
                        modConstructor::onRegisterItemModels).registerForEvent(event);
            });
            eventBus.addListener((final RegisterRangeSelectItemModelPropertyEvent event) -> {
                AbstractNeoForgeContext.computeIfAbsent(itemModelsContext,
                        ItemModelsContextNeoForgeImpl::new,
                        modConstructor::onRegisterItemModels).registerForEvent(event);
            });
            eventBus.addListener((final RegisterMenuScreensEvent event) -> {
                modConstructor.onRegisterMenuScreens(new MenuScreensContextNeoForgeImpl(event));
            });
            eventBus.addListener((final EntityRenderersEvent.RegisterRenderers event) -> {
                modConstructor.onRegisterEntityRenderers(new EntityRenderersContextNeoForgeImpl(event));
                modConstructor.onRegisterBlockEntityRenderers(new BlockEntityRenderersContextNeoForgeImpl(event));
            });
            eventBus.addListener((final RegisterClientTooltipComponentFactoriesEvent event) -> {
                modConstructor.onRegisterClientTooltipComponents(new ClientTooltipComponentsContextNeoForgeImpl(event));
            });
            eventBus.addListener((final RegisterParticleProvidersEvent event) -> {
                modConstructor.onRegisterParticleProviders(new ParticleProvidersContextNeoForgeImpl(event));
            });
            eventBus.addListener((final EntityRenderersEvent.RegisterLayerDefinitions event) -> {
                modConstructor.onRegisterLayerDefinitions(new LayerDefinitionsContextNeoForgeImpl(event));
            });
            eventBus.addListener((final ModelEvent.ModifyBakingResult event) -> {
                modConstructor.onRegisterBlockStateResolver(new BlockStateResolverContextNeoForgeImpl(event));
            });
            eventBus.addListener((final RegisterEntitySpectatorShadersEvent event) -> {
                modConstructor.onRegisterEntitySpectatorShaders(new EntitySpectatorShadersContextNeoForgeImpl(event));
            });
            eventBus.addListener((final RegisterSpecialBlockModelRendererEvent event) -> {
                modConstructor.onRegisterSpecialBlockModelRenderers(new SpecialBlockModelRenderersContextNeoForgeImpl(
                        event));
            });
            eventBus.addListener((final EntityRenderersEvent.CreateSkullModels event) -> {
                AbstractNeoForgeContext.computeIfAbsent(skullRenderersContext,
                        SkullRenderersContextNeoForgeImpl::new,
                        modConstructor::onRegisterSkullRenderers).registerForEvent(event);
            });
            eventBus.addListener((final EntityRenderersEvent.AddLayers event) -> {
                modConstructor.onRegisterLivingEntityRenderLayers(new LivingEntityRenderLayersContextNeoForgeImpl(event));
            });
            eventBus.addListener((final RegisterKeyMappingsEvent event) -> {
                modConstructor.onRegisterKeyMappings(new KeyMappingsContextNeoForgeImpl(event));
            });
            eventBus.addListener((final RegisterColorHandlersEvent.Block event) -> {
                modConstructor.onRegisterBlockColorProviders(new BlockBlockColorsContextNeoForgeImpl(event));
            });
            eventBus.addListener((final AddPackFindersEvent event) -> {
                if (event.getPackType() == PackType.CLIENT_RESOURCES) {
                    modConstructor.onAddResourcePackFinders(new ResourcePackSourcesContextNeoForgeImpl(event));
                }
            });
            eventBus.addListener((final RegisterRenderBuffersEvent event) -> {
                modConstructor.onRegisterRenderBuffers(new RenderBuffersContextNeoForgeImpl(event));
            });
            eventBus.addListener((final RegisterRenderPipelinesEvent event) -> {
                modConstructor.onRegisterRenderPipelines(new RenderPipelinesContextNeoForgeImpl(event));
            });
            eventBus.addListener((final RegisterGuiLayersEvent event) -> {
                modConstructor.onRegisterGuiLayers(new GuiLayersContextNeoForgeImpl(event));
            });
            eventBus.addListener((final RegisterPictureInPictureRenderersEvent event) -> {
                modConstructor.onRegisterPictureInPictureRenderers(new PictureInPictureRendererContextNeoForgeImpl(event));
            });
        });
    }
}
