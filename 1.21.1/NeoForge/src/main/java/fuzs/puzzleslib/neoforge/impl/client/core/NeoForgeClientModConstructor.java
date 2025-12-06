package fuzs.puzzleslib.neoforge.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.impl.client.core.context.BlockRenderTypesContextImpl;
import fuzs.puzzleslib.impl.client.core.context.FluidRenderTypesContextImpl;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.client.core.context.*;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class NeoForgeClientModConstructor implements ModConstructorImpl<ClientModConstructor> {

    @Override
    public void construct(String modId, ClientModConstructor modConstructor, Set<ContentRegistrationFlags> contentRegistrationFlags) {
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            modConstructor.onConstructMod();
            List<ResourceManagerReloadListener> dynamicRenderers = new ArrayList<>();
            eventBus.addListener((final FMLClientSetupEvent event) -> {
                // need to run this deferred as most registries here do not use concurrent maps
                event.enqueueWork(() -> {
                    modConstructor.onClientSetup();
                    modConstructor.onRegisterItemModelProperties(new ItemModelPropertiesContextNeoForgeImpl());
                    modConstructor.onRegisterBlockRenderTypes(new BlockRenderTypesContextImpl());
                    modConstructor.onRegisterFluidRenderTypes(new FluidRenderTypesContextImpl());
                });
            });
            eventBus.addListener((final RegisterMenuScreensEvent event) -> {
                modConstructor.onRegisterMenuScreens(new MenuScreensContextNeoForgeImpl(event));
            });
            eventBus.addListener((final EntityRenderersEvent.RegisterRenderers event) -> {
                modConstructor.onRegisterEntityRenderers(new EntityRenderersContextNeoForgeImpl(event::registerEntityRenderer));
                modConstructor.onRegisterBlockEntityRenderers(new BlockEntityRenderersContextNeoForgeImpl(event::registerBlockEntityRenderer));
            });
            eventBus.addListener((final RegisterClientTooltipComponentFactoriesEvent event) -> {
                modConstructor.onRegisterClientTooltipComponents(new ClientTooltipComponentsContextNeoForgeImpl(event::register));
            });
            eventBus.addListener((final RegisterParticleProvidersEvent event) -> {
                modConstructor.onRegisterParticleProviders(new ParticleProvidersContextNeoForgeImpl(event));
            });
            eventBus.addListener((final EntityRenderersEvent.RegisterLayerDefinitions event) -> {
                modConstructor.onRegisterLayerDefinitions(new LayerDefinitionsContextNeoForgeImpl(event::registerLayerDefinition));
            });
            eventBus.addListener((final ModelEvent.RegisterAdditional event) -> {
                modConstructor.onRegisterAdditionalModels(new AdditionalModelsContextNeoForgeImpl(event::register));
            });
            eventBus.addListener((final RegisterItemDecorationsEvent event) -> {
                modConstructor.onRegisterItemDecorations(new ItemDecorationContextNeoForgeImpl(event::register));
            });
            eventBus.addListener((final RegisterEntitySpectatorShadersEvent event) -> {
                modConstructor.onRegisterEntitySpectatorShaders(new EntitySpectatorShaderContextNeoForgeImpl(event::register));
            });
            eventBus.addListener((final EntityRenderersEvent.CreateSkullModels event) -> {
                modConstructor.onRegisterSkullRenderers(new SkullRenderersContextNeoForgeImpl(event.getEntityModelSet(),
                        event::registerSkullModel));
            });
            eventBus.addListener((final RegisterClientReloadListenersEvent event) -> {
                if (contentRegistrationFlags.contains(ContentRegistrationFlags.DYNAMIC_RENDERERS)) {
                    event.registerReloadListener(ForwardingReloadListenerHelper.fromResourceManagerReloadListeners(
                            ResourceLocationHelper.fromNamespaceAndPath(modId, "built_in_model_item_renderers"),
                            dynamicRenderers));
                }
            });
            eventBus.addListener((final EntityRenderersEvent.AddLayers event) -> {
                modConstructor.onRegisterLivingEntityRenderLayers(new LivingEntityRenderLayersContextNeoForgeImpl(event));
            });
            eventBus.addListener((final RegisterKeyMappingsEvent event) -> {
                modConstructor.onRegisterKeyMappings(new KeyMappingsContextNeoForgeImpl(event::register));
            });
            eventBus.addListener((final RegisterColorHandlersEvent.Block event) -> {
                modConstructor.onRegisterBlockColorProviders(new BlockColorProvidersContextNeoForgeImpl(event::register,
                        event.getBlockColors()));
            });
            eventBus.addListener((final RegisterColorHandlersEvent.Item event) -> {
                modConstructor.onRegisterItemColorProviders(new ItemColorProvidersContextNeoForgeImpl(event::register,
                        event.getItemColors()));
            });
            eventBus.addListener((final AddPackFindersEvent event) -> {
                if (event.getPackType() == PackType.CLIENT_RESOURCES) {
                    modConstructor.onAddResourcePackFinders(new ResourcePackSourcesContextNeoForgeImpl(event));
                }
            });
            eventBus.addListener((final RegisterShadersEvent event) -> {
                modConstructor.onRegisterCoreShaders(new CoreShadersContextNeoForgeImpl(event::registerShader,
                        event.getResourceProvider()));
            });
            eventBus.addListener((final RegisterRenderBuffersEvent event) -> {
                modConstructor.onRegisterRenderBuffers(new RenderBuffersContextNeoForgeImpl(event::registerRenderBuffer));
            });
            eventBus.addListener((final RegisterClientExtensionsEvent event) -> {
                modConstructor.onRegisterBuiltinModelItemRenderers(new BuiltinModelItemRendererContextNeoForgeImpl(event::registerItem,
                        modId,
                        dynamicRenderers));
            });
        });
    }
}
