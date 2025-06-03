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
            modConstructor.onRegisterGuiLayers(new GuiLayersContextNeoForgeImpl(eventBus));
            eventBus.addListener((final FMLClientSetupEvent evt) -> {
                evt.enqueueWork(() -> {
                    modConstructor.onClientSetup();
                    AbstractNeoForgeContext.computeIfAbsent(skullRenderersContext,
                            SkullRenderersContextNeoForgeImpl::new,
                            modConstructor::onRegisterSkullRenderers).registerForEvent(evt);
                });
            });
            // let this run after other mods, some of our mods are likely going to reference what other mods have registered
            eventBus.addListener(EventPriority.LOW, (final FMLClientSetupEvent evt) -> {
                // need to run this deferred as the underlying registries do not use concurrent maps
                evt.enqueueWork(() -> {
                    modConstructor.onRegisterBlockRenderTypes(new BlockRenderTypesContextNeoForgeImpl());
                    modConstructor.onRegisterFluidRenderTypes(new FluidRenderTypesContextNeoForgeImpl());
                });
            });
            eventBus.addListener((final RegisterItemModelsEvent evt) -> {
                AbstractNeoForgeContext.computeIfAbsent(itemModelsContext,
                        ItemModelsContextNeoForgeImpl::new,
                        modConstructor::onRegisterItemModels).registerForEvent(evt);
            });
            eventBus.addListener((final RegisterSpecialModelRendererEvent evt) -> {
                AbstractNeoForgeContext.computeIfAbsent(itemModelsContext,
                        ItemModelsContextNeoForgeImpl::new,
                        modConstructor::onRegisterItemModels).registerForEvent(evt);
            });
            eventBus.addListener((final RegisterColorHandlersEvent.ItemTintSources evt) -> {
                AbstractNeoForgeContext.computeIfAbsent(itemModelsContext,
                        ItemModelsContextNeoForgeImpl::new,
                        modConstructor::onRegisterItemModels).registerForEvent(evt);
            });
            eventBus.addListener((final RegisterSelectItemModelPropertyEvent evt) -> {
                AbstractNeoForgeContext.computeIfAbsent(itemModelsContext,
                        ItemModelsContextNeoForgeImpl::new,
                        modConstructor::onRegisterItemModels).registerForEvent(evt);
            });
            eventBus.addListener((final RegisterConditionalItemModelPropertyEvent evt) -> {
                AbstractNeoForgeContext.computeIfAbsent(itemModelsContext,
                        ItemModelsContextNeoForgeImpl::new,
                        modConstructor::onRegisterItemModels).registerForEvent(evt);
            });
            eventBus.addListener((final RegisterRangeSelectItemModelPropertyEvent evt) -> {
                AbstractNeoForgeContext.computeIfAbsent(itemModelsContext,
                        ItemModelsContextNeoForgeImpl::new,
                        modConstructor::onRegisterItemModels).registerForEvent(evt);
            });
            eventBus.addListener((final RegisterMenuScreensEvent evt) -> {
                modConstructor.onRegisterMenuScreens(new MenuScreensContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final EntityRenderersEvent.RegisterRenderers evt) -> {
                modConstructor.onRegisterEntityRenderers(new EntityRenderersContextNeoForgeImpl(evt));
                modConstructor.onRegisterBlockEntityRenderers(new BlockEntityRenderersContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final RegisterClientTooltipComponentFactoriesEvent evt) -> {
                modConstructor.onRegisterClientTooltipComponents(new ClientTooltipComponentsContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final RegisterParticleProvidersEvent evt) -> {
                modConstructor.onRegisterParticleProviders(new ParticleProvidersContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final EntityRenderersEvent.RegisterLayerDefinitions evt) -> {
                modConstructor.onRegisterLayerDefinitions(new LayerDefinitionsContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final ModelEvent.ModifyBakingResult evt) -> {
                modConstructor.onRegisterBlockStateResolver(new BlockStateResolverContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final RegisterItemDecorationsEvent evt) -> {
                modConstructor.onRegisterItemDecorations(new ItemDecorationsContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final RegisterEntitySpectatorShadersEvent evt) -> {
                modConstructor.onRegisterEntitySpectatorShaders(new EntitySpectatorShadersContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final RegisterSpecialBlockModelRendererEvent evt) -> {
                modConstructor.onRegisterSpecialBlockModelRenderers(new SpecialBlockModelRenderersContextNeoForgeImpl(
                        evt));
            });
            eventBus.addListener((final EntityRenderersEvent.CreateSkullModels evt) -> {
                AbstractNeoForgeContext.computeIfAbsent(skullRenderersContext,
                        SkullRenderersContextNeoForgeImpl::new,
                        modConstructor::onRegisterSkullRenderers).registerForEvent(evt);
            });
            eventBus.addListener((final EntityRenderersEvent.AddLayers evt) -> {
                modConstructor.onRegisterLivingEntityRenderLayers(new LivingEntityRenderLayersContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final RegisterKeyMappingsEvent evt) -> {
                modConstructor.onRegisterKeyMappings(new KeyMappingsContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final RegisterColorHandlersEvent.Block evt) -> {
                modConstructor.onRegisterBlockColorProviders(new BlockBlockColorsContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final AddPackFindersEvent evt) -> {
                if (evt.getPackType() == PackType.CLIENT_RESOURCES) {
                    modConstructor.onAddResourcePackFinders(new ResourcePackSourcesContextNeoForgeImpl(evt));
                }
            });
            eventBus.addListener((final RegisterRenderBuffersEvent evt) -> {
                modConstructor.onRegisterRenderBuffers(new RenderBuffersContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final RegisterRenderPipelinesEvent evt) -> {
                modConstructor.onRegisterRenderPipelines(new RenderPipelinesContextNeoForgeImpl(evt));
            });
        });
    }
}
