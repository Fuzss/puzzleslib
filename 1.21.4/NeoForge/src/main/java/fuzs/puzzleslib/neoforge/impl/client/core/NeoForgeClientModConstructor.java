package fuzs.puzzleslib.neoforge.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.impl.client.core.context.BlockRenderTypesContextImpl;
import fuzs.puzzleslib.impl.client.core.context.FluidRenderTypesContextImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.client.core.context.*;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.AddPackFindersEvent;

public final class NeoForgeClientModConstructor {

    private NeoForgeClientModConstructor() {
        // NO-OP
    }

    public static void construct(ClientModConstructor modConstructor, String modId) {
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            modConstructor.onConstructMod();
            eventBus.addListener((final FMLClientSetupEvent evt) -> {
                evt.enqueueWork(() -> {
                    modConstructor.onClientSetup();
                    // need to run this deferred as the underlying registries do not use concurrent maps
                    modConstructor.onRegisterBlockRenderTypes(new BlockRenderTypesContextImpl());
                    modConstructor.onRegisterFluidRenderTypes(new FluidRenderTypesContextImpl());
                });
            });
            eventBus.addListener((final RegisterMenuScreensEvent evt) -> {
                modConstructor.onRegisterMenuScreens(new MenuScreensContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final EntityRenderersEvent.RegisterRenderers evt) -> {
                modConstructor.onRegisterEntityRenderers(new EntityRenderersContextNeoForgeImpl(evt::registerEntityRenderer));
                modConstructor.onRegisterBlockEntityRenderers(new BlockEntityRenderersContextNeoForgeImpl(evt::registerBlockEntityRenderer));
            });
            eventBus.addListener((final RegisterClientTooltipComponentFactoriesEvent evt) -> {
                modConstructor.onRegisterClientTooltipComponents(new ClientTooltipComponentsContextNeoForgeImpl(evt::register));
            });
            eventBus.addListener((final RegisterParticleProvidersEvent evt) -> {
                modConstructor.onRegisterParticleProviders(new ParticleProvidersContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final EntityRenderersEvent.RegisterLayerDefinitions evt) -> {
                modConstructor.onRegisterLayerDefinitions(new LayerDefinitionsContextNeoForgeImpl(evt::registerLayerDefinition));
            });
            eventBus.addListener((final ModelEvent.RegisterAdditional evt) -> {
                modConstructor.onRegisterAdditionalModels(new AdditionalModelsContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final ModelEvent.ModifyBakingResult evt) -> {
                modConstructor.onRegisterBlockStateResolver(new BlockStateResolverContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final RegisterItemDecorationsEvent evt) -> {
                modConstructor.onRegisterItemDecorations(new ItemDecorationsContextNeoForgeImpl(evt::register));
            });
            eventBus.addListener((final RegisterEntitySpectatorShadersEvent evt) -> {
                modConstructor.onRegisterEntitySpectatorShaders(new EntitySpectatorShadersContextNeoForgeImpl(evt::register));
            });
            eventBus.addListener((final RegisterSpecialModelRendererEvent evt) -> {
                modConstructor.onRegisterSpecialBlockModelTypes(new SpecialBlockModelTypesContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final RegisterSpecialBlockModelRendererEvent evt) -> {
                modConstructor.onRegisterSpecialBlockModelRenderers(new SpecialBlockModelRenderersContextNeoForgeImpl(
                        evt));
            });
            eventBus.addListener((final EntityRenderersEvent.CreateSkullModels evt) -> {
                modConstructor.onRegisterSkullRenderers(new SkullRenderersContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final EntityRenderersEvent.AddLayers evt) -> {
                modConstructor.onRegisterLivingEntityRenderLayers(new LivingEntityRenderLayersContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final RegisterKeyMappingsEvent evt) -> {
                modConstructor.onRegisterKeyMappings(new KeyMappingsContextNeoForgeImpl(evt::register));
            });
            eventBus.addListener((final RegisterColorHandlersEvent.Block evt) -> {
                modConstructor.onRegisterBlockColorProviders(new BlockBlockColorsContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final AddPackFindersEvent evt) -> {
                if (evt.getPackType() == PackType.CLIENT_RESOURCES) {
                    modConstructor.onAddResourcePackFinders(new ResourcePackSourcesContextNeoForgeImpl(evt::addRepositorySource));
                }
            });
            eventBus.addListener((final RegisterShadersEvent evt) -> {
                modConstructor.onRegisterCoreShaders(new CoreShadersContextNeoForgeImpl(evt::registerShader));
            });
            eventBus.addListener((final RegisterRenderBuffersEvent evt) -> {
                modConstructor.onRegisterRenderBuffers(new RenderBuffersContextNeoForgeImpl(evt::registerRenderBuffer));
            });
        });
    }
}
