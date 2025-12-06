package fuzs.puzzleslib.fabric.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.core.v1.context.CoreShadersContext;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.fabric.api.core.v1.resources.FabricReloadListener;
import fuzs.puzzleslib.fabric.impl.client.core.context.*;
import fuzs.puzzleslib.impl.client.core.context.BlockRenderTypesContextImpl;
import fuzs.puzzleslib.impl.client.core.context.FluidRenderTypesContextImpl;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class FabricClientModConstructor implements ModConstructorImpl<ClientModConstructor> {

    @Override
    public void construct(String modId, ClientModConstructor modConstructor, Set<ContentRegistrationFlags> contentRegistrationFlags) {
        modConstructor.onConstructMod();
        modConstructor.onClientSetup();
        modConstructor.onRegisterEntityRenderers(new EntityRenderersContextFabricImpl());
        modConstructor.onRegisterBlockEntityRenderers(new BlockEntityRenderersContextFabricImpl());
        modConstructor.onRegisterClientTooltipComponents(new ClientTooltipComponentsContextFabricImpl());
        modConstructor.onRegisterParticleProviders(new ParticleProvidersContextFabricImpl());
        modConstructor.onRegisterMenuScreens(new MenuScreensContextFabricImpl());
        modConstructor.onRegisterLayerDefinitions(new LayerDefinitionsContextFabricImpl());
        modConstructor.onRegisterAdditionalModels(new AdditionalModelsContextFabricImpl());
        modConstructor.onRegisterItemModelProperties(new ItemModelPropertiesContextFabricImpl());
        modConstructor.onRegisterEntitySpectatorShaders(new EntitySpectatorShaderContextFabricImpl());
        modConstructor.onRegisterRenderBuffers(new RenderBuffersContextFabricImpl());
        List<ResourceManagerReloadListener> dynamicRenderers = new ArrayList<>();
        ((Consumer<BuiltinModelItemRendererContext>) modConstructor::onRegisterBuiltinModelItemRenderers).accept(new BuiltinModelItemRendererContextFabricImpl(
                modId,
                dynamicRenderers));
        // do not punish ContentRegistrationFlags#DYNAMIC_RENDERERS being absent as not every built-in item renderer needs to reload
        if (contentRegistrationFlags.contains(ContentRegistrationFlags.DYNAMIC_RENDERERS)) {
            ResourceLocation identifier = ResourceLocationHelper.fromNamespaceAndPath(modId,
                    "built_in_model_item_renderers");
            IdentifiableResourceReloadListener reloadListener = new FabricReloadListener(ForwardingReloadListenerHelper.fromResourceManagerReloadListeners(
                    identifier,
                    dynamicRenderers));
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(reloadListener);
        }
        modConstructor.onRegisterLivingEntityRenderLayers(new LivingEntityRenderLayersContextFabricImpl());
        modConstructor.onRegisterItemDecorations(new ItemDecorationContextFabricImpl());
        modConstructor.onRegisterSkullRenderers(new SkullRenderersContextFabricImpl());
        modConstructor.onRegisterKeyMappings(new KeyMappingsContextFabricImpl());
        modConstructor.onAddResourcePackFinders(new ResourcePackSourcesContextFabricImpl());
        // run this as late as possible and not during client init so that maps are already fully populated with vanilla content
        ClientLifecycleEvents.CLIENT_STARTED.register((Minecraft client) -> {
            modConstructor.onRegisterBlockRenderTypes(new BlockRenderTypesContextImpl());
            modConstructor.onRegisterFluidRenderTypes(new FluidRenderTypesContextImpl());
            modConstructor.onRegisterBlockColorProviders(new BlockColorProvidersContextFabricImpl());
            modConstructor.onRegisterItemColorProviders(new ItemColorProvidersContextFabricImpl());
        });
        CoreShaderRegistrationCallback.EVENT.register((CoreShaderRegistrationCallback.RegistrationContext context) -> {
            ((Consumer<CoreShadersContext>) modConstructor::onRegisterCoreShaders).accept(new CoreShadersContextFabricImpl(
                    context));
        });
    }
}
