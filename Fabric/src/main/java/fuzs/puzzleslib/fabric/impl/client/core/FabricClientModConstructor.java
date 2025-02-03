package fuzs.puzzleslib.fabric.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.fabric.impl.client.core.context.*;
import fuzs.puzzleslib.impl.client.core.context.BlockRenderTypesContextImpl;
import fuzs.puzzleslib.impl.client.core.context.FluidRenderTypesContextImpl;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;

public final class FabricClientModConstructor {

    private FabricClientModConstructor() {
        // NO-OP
    }

    public static void construct(ClientModConstructor modConstructor, String modId) {
        modConstructor.onConstructMod();
        modConstructor.onClientSetup();
        modConstructor.onRegisterEntityRenderers(new EntityRenderersContextFabricImpl());
        modConstructor.onRegisterBlockEntityRenderers(new BlockEntityRenderersContextFabricImpl());
        modConstructor.onRegisterClientTooltipComponents(new ClientTooltipComponentsContextFabricImpl());
        modConstructor.onRegisterParticleProviders(new ParticleProvidersContextFabricImpl());
        modConstructor.onRegisterMenuScreens(new MenuScreensContextFabricImpl());
        modConstructor.onRegisterLayerDefinitions(new LayerDefinitionsContextFabricImpl());
        modConstructor.onRegisterAdditionalModels(new AdditionalModelsContextFabricImpl());
        modConstructor.onRegisterItemDecorations(new ItemDecorationsContextFabricImpl());
        modConstructor.onRegisterEntitySpectatorShaders(new EntitySpectatorShadersContextFabricImpl());
        modConstructor.onRegisterSpecialBlockModelTypes(new SpecialBlockModelTypesContextFabricImpl());
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
