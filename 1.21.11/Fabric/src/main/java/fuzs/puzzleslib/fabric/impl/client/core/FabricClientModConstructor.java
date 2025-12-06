package fuzs.puzzleslib.fabric.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.fabric.impl.client.core.context.*;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
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
        modConstructor.onRegisterBlockStateResolver(new BlockStateResolverContextFabricImpl());
        modConstructor.onRegisterEntitySpectatorShaders(new EntitySpectatorShadersContextFabricImpl());
        modConstructor.onRegisterSpecialBlockModelRenderers(new SpecialBlockModelRenderersContextFabricImpl());
        modConstructor.onRegisterSkullRenderers(new SkullRenderersContextFabricImpl());
        modConstructor.onRegisterKeyMappings(new KeyMappingsContextFabricImpl());
        modConstructor.onAddResourcePackFinders(new ResourcePackSourcesContextFabricImpl());
        modConstructor.onRegisterRenderBuffers(new RenderBuffersContextFabricImpl());
        modConstructor.onRegisterRenderPipelines(new RenderPipelinesContextFabricImpl());
        modConstructor.onRegisterGuiLayers(new GuiLayersContextFabricImpl());
        modConstructor.onRegisterPictureInPictureRenderers(new PictureInPictureRenderersContextFabricImpl());
        modConstructor.onAddResourcePackReloadListeners(new ResourcePackReloadListenersContextFabricImpl());
        ClientLifecycleEvents.CLIENT_STARTED.register((Minecraft minecraft) -> {
            // run this as late as possible and not during client init, so that maps are already fully populated with vanilla and modded content
            modConstructor.onRegisterBlockRenderTypes(new BlockRenderTypesContextFabricImpl());
            modConstructor.onRegisterFluidRenderTypes(new FluidRenderTypesContextFabricImpl());
            modConstructor.onRegisterBlockColorProviders(new BlockBlockColorsContextFabricImpl());
        });
    }
}
