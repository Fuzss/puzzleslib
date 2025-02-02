package fuzs.puzzleslib.neoforge.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.impl.client.core.context.BlockRenderTypesContextImpl;
import fuzs.puzzleslib.impl.client.core.context.FluidRenderTypesContextImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.client.core.context.*;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class NeoForgeClientModConstructor {

    private NeoForgeClientModConstructor() {
        // NO-OP
    }


    public static void construct(ClientModConstructor constructor, String modId, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent(eventBus -> {
            constructor.onConstructMod();
            registerLoadingHandlers(constructor, modId, eventBus, availableFlags, flagsToHandle);
        });
    }

    private static void registerLoadingHandlers(ClientModConstructor constructor, String modId, IEventBus eventBus, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        List<ResourceManagerReloadListener> dynamicRenderers = new ArrayList<>();
        eventBus.addListener((final FMLClientSetupEvent evt) -> {
            // need to run this deferred as most registries here do not use concurrent maps
            evt.enqueueWork(() -> {
                constructor.onClientSetup();
                constructor.onRegisterItemModelProperties(new ItemModelPropertiesContextNeoForgeImpl());
                constructor.onRegisterBlockRenderTypes(new BlockRenderTypesContextImpl());
                constructor.onRegisterFluidRenderTypes(new FluidRenderTypesContextImpl());
            });
        });
        eventBus.addListener((final RegisterMenuScreensEvent evt) -> {
            constructor.onRegisterMenuScreens(new MenuScreensContextNeoForgeImpl(evt));
        });
        eventBus.addListener((final EntityRenderersEvent.RegisterRenderers evt) -> {
            constructor.onRegisterEntityRenderers(new EntityRenderersContextNeoForgeImpl(evt::registerEntityRenderer));
            constructor.onRegisterBlockEntityRenderers(new BlockEntityRenderersContextNeoForgeImpl(evt::registerBlockEntityRenderer));
        });
        eventBus.addListener((final RegisterClientTooltipComponentFactoriesEvent evt) -> {
            constructor.onRegisterClientTooltipComponents(new ClientTooltipComponentsContextNeoForgeImpl(evt::register));
        });
        eventBus.addListener((final RegisterParticleProvidersEvent evt) -> {
            constructor.onRegisterParticleProviders(new ParticleProvidersContextNeoForgeImpl(evt));
        });
        eventBus.addListener((final EntityRenderersEvent.RegisterLayerDefinitions evt) -> {
            constructor.onRegisterLayerDefinitions(new LayerDefinitionsContextNeoForgeImpl(evt::registerLayerDefinition));
        });
        eventBus.addListener((final ModelEvent.RegisterAdditional evt) -> {
            constructor.onRegisterAdditionalModels(new AdditionalModelsContextNeoForgeImpl(evt::register));
        });
        eventBus.addListener((final RegisterItemDecorationsEvent evt) -> {
            constructor.onRegisterItemDecorations(new ItemDecorationContextNeoForgeImpl(evt::register));
        });
        eventBus.addListener((final RegisterEntitySpectatorShadersEvent evt) -> {
            constructor.onRegisterEntitySpectatorShaders(new EntitySpectatorShaderContextNeoForgeImpl(evt::register));
        });
        eventBus.addListener((final EntityRenderersEvent.CreateSkullModels evt) -> {
            constructor.onRegisterSkullRenderers(new SkullRenderersContextNeoForgeImpl(evt.getEntityModelSet(),
                    evt::registerSkullModel));
        });
        eventBus.addListener((final RegisterClientReloadListenersEvent evt) -> {
            if (availableFlags.contains(ContentRegistrationFlags.DYNAMIC_RENDERERS)) {
                evt.registerReloadListener(ForwardingReloadListenerHelper.fromResourceManagerReloadListeners(
                        ResourceLocationHelper.fromNamespaceAndPath(modId, "built_in_model_item_renderers"),
                        dynamicRenderers));
            }
        });
        eventBus.addListener((final EntityRenderersEvent.AddLayers evt) -> {
            constructor.onRegisterLivingEntityRenderLayers(new LivingEntityRenderLayersContextNeoForgeImpl(evt));
        });
        eventBus.addListener((final RegisterKeyMappingsEvent evt) -> {
            constructor.onRegisterKeyMappings(new KeyMappingsContextNeoForgeImpl(evt::register));
        });
        eventBus.addListener((final RegisterColorHandlersEvent.Block evt) -> {
            constructor.onRegisterBlockColorProviders(new BlockColorProvidersContextNeoForgeImpl(evt::register,
                    evt.getBlockColors()));
        });
        eventBus.addListener((final RegisterColorHandlersEvent.Item evt) -> {
            constructor.onRegisterItemColorProviders(new ItemColorProvidersContextNeoForgeImpl(evt::register,
                    evt.getItemColors()));
        });
        eventBus.addListener((final AddPackFindersEvent evt) -> {
            if (evt.getPackType() == PackType.CLIENT_RESOURCES) {
                constructor.onAddResourcePackFinders(new ResourcePackSourcesContextNeoForgeImpl(evt::addRepositorySource));
            }
        });
        eventBus.addListener((final RegisterShadersEvent evt) -> {
            constructor.onRegisterCoreShaders(new CoreShadersContextNeoForgeImpl(evt::registerShader));
        });
        eventBus.addListener((final RegisterRenderBuffersEvent evt) -> {
            constructor.onRegisterRenderBuffers(new RenderBuffersContextNeoForgeImpl(evt::registerRenderBuffer));
        });
        eventBus.addListener(EventPriority.LOW, (final RegisterClientExtensionsEvent evt) -> {
            constructor.onRegisterBuiltinModelItemRenderers(new BuiltinModelItemRendererContextNeoForgeImpl((clientItemExtensions, item) -> {
                if (!evt.isItemRegistered(item)) {
                    evt.registerItem(clientItemExtensions, item);
                }
            }, modId, dynamicRenderers));
        });
    }
}
