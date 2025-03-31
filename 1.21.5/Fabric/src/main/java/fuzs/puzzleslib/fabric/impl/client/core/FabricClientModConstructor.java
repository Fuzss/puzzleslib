package fuzs.puzzleslib.fabric.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.fabric.impl.client.core.context.*;
import fuzs.puzzleslib.impl.client.core.context.BlockRenderTypesContextImpl;
import fuzs.puzzleslib.impl.client.core.context.FluidRenderTypesContextImpl;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.Minecraft;

public final class FabricClientModConstructor implements ModConstructorImpl<ClientModConstructor> {

    @Override
    public void construct(String modId, ClientModConstructor modConstructor) {
        modConstructor.onConstructMod();
        modConstructor.onClientSetup();
        modConstructor.onRegisterItemModels(new ItemModelsContextFabricImpl());
        modConstructor.onRegisterEntityRenderers(new EntityRenderersContextFabricImpl());
        modConstructor.onRegisterBlockEntityRenderers(new BlockEntityRenderersContextFabricImpl());
        modConstructor.onRegisterClientTooltipComponents(new ClientTooltipComponentsContextFabricImpl());
        modConstructor.onRegisterParticleProviders(new ParticleProvidersContextFabricImpl());
        modConstructor.onRegisterMenuScreens(new MenuScreensContextFabricImpl());
        modConstructor.onRegisterLayerDefinitions(new LayerDefinitionsContextFabricImpl());
        ModelLoadingPlugin.register((ModelLoadingPlugin.Context context) -> {
            modConstructor.onRegisterAdditionalModels(new AdditionalModelsContextFabricImpl(context));
        });
        modConstructor.onRegisterBlockStateResolver(new BlockStateResolverContextFabricImpl());
        modConstructor.onRegisterItemDecorations(new ItemDecorationsContextFabricImpl());
        modConstructor.onRegisterEntitySpectatorShaders(new EntitySpectatorShadersContextFabricImpl());
        modConstructor.onRegisterSpecialBlockModelRenderers(new SpecialBlockModelRenderersContextFabricImpl());
        modConstructor.onRegisterSkullRenderers(new SkullRenderersContextFabricImpl());
        modConstructor.onRegisterLivingEntityRenderLayers(new LivingEntityRenderLayersContextFabricImpl());
        modConstructor.onRegisterKeyMappings(new KeyMappingsContextFabricImpl());
        modConstructor.onAddResourcePackFinders(new ResourcePackSourcesContextFabricImpl());
        modConstructor.onRegisterCoreShaders(new CoreShadersContextFabricImpl());
        modConstructor.onRegisterRenderBuffers(new RenderBuffersContextFabricImpl());
        ClientLifecycleEvents.CLIENT_STARTED.register((Minecraft client) -> {
            // run this as late as possible and not during client init so that maps are already fully populated with vanilla content
            modConstructor.onRegisterBlockRenderTypes(new BlockRenderTypesContextImpl());
            modConstructor.onRegisterFluidRenderTypes(new FluidRenderTypesContextImpl());
            modConstructor.onRegisterBlockColorProviders(new BlockBlockColorsContextFabricImpl());
        });
    }
}
