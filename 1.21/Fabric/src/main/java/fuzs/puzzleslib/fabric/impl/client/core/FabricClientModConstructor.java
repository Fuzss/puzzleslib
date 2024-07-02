package fuzs.puzzleslib.fabric.impl.client.core;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.core.v1.context.CoreShadersContext;
import fuzs.puzzleslib.api.client.core.v1.context.ParticleProvidersContext;
import fuzs.puzzleslib.api.client.particle.v1.ClientParticleTypes;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import fuzs.puzzleslib.fabric.api.core.v1.resources.FabricReloadListener;
import fuzs.puzzleslib.fabric.impl.client.core.context.*;
import fuzs.puzzleslib.fabric.impl.core.context.AddReloadListenersContextFabricImpl;
import fuzs.puzzleslib.impl.client.core.context.BlockRenderTypesContextImpl;
import fuzs.puzzleslib.impl.client.core.context.FluidRenderTypesContextImpl;
import fuzs.puzzleslib.impl.client.particle.ClientParticleTypesImpl;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class FabricClientModConstructor {

    private FabricClientModConstructor() {

    }

    public static void construct(ClientModConstructor constructor, String modId, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        constructor.onConstructMod();
        constructor.onClientSetup();
        constructor.onRegisterEntityRenderers(new EntityRenderersContextFabricImpl());
        constructor.onRegisterBlockEntityRenderers(new BlockEntityRenderersContextFabricImpl());
        constructor.onRegisterClientTooltipComponents(new ClientTooltipComponentsContextFabricImpl());
        registerClientParticleTypesManager(modId, constructor::onRegisterParticleProviders, flagsToHandle);
        constructor.onRegisterMenuScreens(new MenuScreensContextFabricImpl());
        constructor.onRegisterLayerDefinitions(new LayerDefinitionsContextFabricImpl());
        constructor.onRegisterAdditionalModels(new AdditionalModelsContextFabricImpl());
        constructor.onRegisterItemModelProperties(new ItemModelPropertiesContextFabricImpl());
        constructor.onRegisterEntitySpectatorShaders(new EntitySpectatorShaderContextFabricImpl());
        constructor.onRegisterRenderBuffers(new RenderBuffersContextFabricImpl());
        registerBuiltinModelItemRenderers(modId, constructor::onRegisterBuiltinModelItemRenderers, availableFlags);
        constructor.onRegisterResourcePackReloadListeners(new AddReloadListenersContextFabricImpl(PackType.CLIENT_RESOURCES, modId));
        constructor.onRegisterLivingEntityRenderLayers(new LivingEntityRenderLayersContextFabricImpl());
        constructor.onRegisterItemDecorations(new ItemDecorationContextFabricImpl());
        constructor.onRegisterSkullRenderers(new SkullRenderersContextFabricImpl());
        constructor.onRegisterKeyMappings(new KeyMappingsContextFabricImpl());
        constructor.onAddResourcePackFinders(new ResourcePackSourcesContextFabricImpl());
        registerRenderProperties(constructor);
        registerCoreShaders(constructor::onRegisterCoreShaders);
    }

    private static void registerClientParticleTypesManager(String modId, Consumer<ParticleProvidersContext> consumer, Set<ContentRegistrationFlags> flagsToHandle) {
        consumer.accept(new ParticleProvidersContextFabricImpl());
        if (flagsToHandle.contains(ContentRegistrationFlags.CLIENT_PARTICLE_TYPES)) {
            ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(modId, "client_particle_types");
            IdentifiableResourceReloadListener reloadListener = new FabricReloadListener(identifier, ((ClientParticleTypesImpl) ClientParticleTypes.INSTANCE).getParticleTypesManager(modId));
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(reloadListener);
        }
    }

    private static void registerBuiltinModelItemRenderers(String modId, Consumer<BuiltinModelItemRendererContext> consumer, Set<ContentRegistrationFlags> availableFlags) {
        List<ResourceManagerReloadListener> dynamicRenderers = Lists.newArrayList();
        consumer.accept(new BuiltinModelItemRendererContextFabricImpl(modId, dynamicRenderers));
        // do not punish ContentRegistrationFlags#DYNAMIC_RENDERERS being absent as not every built-in item renderer needs to reload
        if (availableFlags.contains(ContentRegistrationFlags.DYNAMIC_RENDERERS)) {
            ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(modId, "built_in_model_item_renderers");
            IdentifiableResourceReloadListener reloadListener = new FabricReloadListener(ForwardingReloadListenerHelper.fromResourceManagerReloadListeners(identifier, dynamicRenderers));
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(reloadListener);
        }
    }

    private static void registerRenderProperties(ClientModConstructor constructor) {
        // run this as late as possible and not during client init so that maps are already fully populated with vanilla content
        ClientLifecycleEvents.CLIENT_STARTED.register((Minecraft client) -> {
            constructor.onRegisterBlockRenderTypes(new BlockRenderTypesContextImpl());
            constructor.onRegisterFluidRenderTypes(new FluidRenderTypesContextImpl());
            constructor.onRegisterBlockColorProviders(new BlockColorProvidersContextFabricImpl());
            constructor.onRegisterItemColorProviders(new ItemColorProvidersContextFabricImpl());
        });
    }

    private static void registerCoreShaders(Consumer<CoreShadersContext> modifyBakingResultConsumer) {
        CoreShaderRegistrationCallback.EVENT.register((CoreShaderRegistrationCallback.RegistrationContext context) -> {
            modifyBakingResultConsumer.accept(new CoreShadersContextFabricImpl(context));
        });
    }
}
