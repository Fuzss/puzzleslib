package fuzs.puzzleslib.neoforge.impl.client.core;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.particle.v1.ClientParticleTypes;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import fuzs.puzzleslib.impl.client.core.context.BlockRenderTypesContextImpl;
import fuzs.puzzleslib.impl.client.core.context.FluidRenderTypesContextImpl;
import fuzs.puzzleslib.impl.client.particle.ClientParticleTypesImpl;
import fuzs.puzzleslib.impl.client.particle.ClientParticleTypesManager;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.client.core.context.*;
import fuzs.puzzleslib.neoforge.impl.core.context.AddReloadListenersContextNeoForgeImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.List;
import java.util.Set;

public final class NeoForgeClientModConstructor {

    private NeoForgeClientModConstructor() {

    }


    public static void construct(ClientModConstructor constructor, String modId, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent(eventBus -> {
            registerModHandlers(constructor, modId, eventBus, availableFlags, flagsToHandle);
            constructor.onConstructMod();
        });
    }

    private static void registerModHandlers(ClientModConstructor constructor, String modId, IEventBus eventBus, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        List<ResourceManagerReloadListener> dynamicRenderers = Lists.newArrayList();
        eventBus.addListener((final FMLClientSetupEvent evt) -> {
            // need to run this deferred as most registries here do not use concurrent maps
            evt.enqueueWork(() -> {
                constructor.onClientSetup();
                constructor.onRegisterItemModelProperties(new ItemModelPropertiesContextNeoForgeImpl());
                constructor.onRegisterBuiltinModelItemRenderers(new BuiltinModelItemRendererContextNeoForgeImpl(modId, dynamicRenderers));
                constructor.onRegisterBlockRenderTypes(new BlockRenderTypesContextImpl());
                constructor.onRegisterFluidRenderTypes(new FluidRenderTypesContextImpl());
            });
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
            constructor.onRegisterSkullRenderers(new SkullRenderersContextNeoForgeImpl(evt.getEntityModelSet(), evt::registerSkullModel));
        });
        eventBus.addListener((final RegisterClientReloadListenersEvent evt) -> {
            constructor.onRegisterResourcePackReloadListeners(new AddReloadListenersContextNeoForgeImpl(modId, evt::registerReloadListener));
            if (availableFlags.contains(ContentRegistrationFlags.DYNAMIC_RENDERERS)) {
                evt.registerReloadListener(ForwardingReloadListenerHelper.fromResourceManagerReloadListeners(new ResourceLocation(modId, "built_in_model_item_renderers"), dynamicRenderers));
            }
            if (flagsToHandle.contains(ContentRegistrationFlags.CLIENT_PARTICLE_TYPES)) {
                ClientParticleTypesManager particleTypesManager = ((ClientParticleTypesImpl) ClientParticleTypes.INSTANCE).getParticleTypesManager(modId);
                evt.registerReloadListener(ForwardingReloadListenerHelper.fromReloadListener(new ResourceLocation(modId, "client_particle_types"), particleTypesManager));
            }
        });
        eventBus.addListener((final EntityRenderersEvent.AddLayers evt) -> {
            constructor.onRegisterLivingEntityRenderLayers(new LivingEntityRenderLayersContextNeoForgeImpl(evt));
        });
        eventBus.addListener((final RegisterKeyMappingsEvent evt) -> {
            constructor.onRegisterKeyMappings(new KeyMappingsContextNeoForgeImpl(evt::register));
        });
        eventBus.addListener((final RegisterColorHandlersEvent.Block evt) -> {
            constructor.onRegisterBlockColorProviders(new BlockColorProvidersContextNeoForgeImpl(evt::register, evt.getBlockColors()));
        });
        eventBus.addListener((final RegisterColorHandlersEvent.Item evt) -> {
            constructor.onRegisterItemColorProviders(new ItemColorProvidersContextNeoForgeImpl(evt::register, evt.getItemColors()));
        });
        eventBus.addListener((final AddPackFindersEvent evt) -> {
            if (evt.getPackType() == PackType.CLIENT_RESOURCES) {
                constructor.onAddResourcePackFinders(new ResourcePackSourcesContextNeoForgeImpl(evt::addRepositorySource));
            }
        });
        eventBus.addListener((final RegisterShadersEvent evt) -> {
            constructor.onRegisterCoreShaders(new CoreShadersContextNeoForgeImpl(evt::registerShader, evt.getResourceProvider()));
        });
    }
}
