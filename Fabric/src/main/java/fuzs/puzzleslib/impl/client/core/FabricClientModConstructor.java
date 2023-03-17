package fuzs.puzzleslib.impl.client.core;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.contexts.DynamicModelBakingContext;
import fuzs.puzzleslib.api.client.events.v2.ModelEvents;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.core.contexts.*;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

public final class FabricClientModConstructor {

    private FabricClientModConstructor() {

    }

    public static void construct(ClientModConstructor constructor, String modId, ContentRegistrationFlags... contentRegistrations) {
        if (Strings.isBlank(modId)) throw new IllegalArgumentException("modId cannot be empty");
        PuzzlesLib.LOGGER.info("Constructing client components for mod {}", modId);
        // everything after this is done on Forge using events called by the mod event bus
        // this is done since Forge works with loading stages, Fabric doesn't have those stages, so everything is called immediately
        constructor.onConstructMod();
        constructor.onClientSetup(Runnable::run);
        constructor.onRegisterEntityRenderers(new EntityRenderersContextFabricImpl());
        constructor.onRegisterBlockEntityRenderers(new BlockEntityRenderersContextFabricImpl());
        constructor.onRegisterClientTooltipComponents(new ClientTooltipComponentsContextFabricImpl());
        constructor.onRegisterParticleProviders(new ParticleProvidersContextFabricImpl());
        constructor.onRegisterLayerDefinitions(new LayerDefinitionsContextFabricImpl());
        constructor.onRegisterSearchTrees(new SearchRegistryContextFabricImpl());
        registerModelBakingListeners(constructor, modId);
        constructor.onRegisterAdditionalModels(new AdditionalModelsContextFabricImpl());
        constructor.onRegisterItemModelProperties(new ItemModelPropertiesContextFabricImpl());
        constructor.onRegisterEntitySpectatorShaders(new EntitySpectatorShaderContextFabricImpl());
        registerBuiltinModelItemRenderers(constructor, modId);
        constructor.onRegisterClientReloadListeners(new ClientReloadListenersContextFabricImpl(modId));
        constructor.onRegisterLivingEntityRenderLayers(new LivingEntityRenderLayersContextFabricImpl());
        constructor.onRegisterItemDecorations(new ItemDecorationContextFabricImpl());
        constructor.onRegisterSkullRenderers(new SkullRenderersContextFabricImpl());
        constructor.onRegisterKeyMappings(new KeyMappingsContextFabricImpl());
        constructor.onRegisterBlockRenderTypes(new BlockRenderTypesContextFabricImpl());
        constructor.onRegisterFluidRenderTypes(new FluidRenderTypesContextFabricImpl());
        constructor.onRegisterBlockColorProviders(new BlockColorProvidersContextFabricImpl());
        constructor.onRegisterItemColorProviders(new ItemColorProvidersContextFabricImpl());
        constructor.onBuildCreativeModeTabContents(new BuildCreativeModeTabContentsContextFabricImpl());
    }

    private static void registerModelBakingListeners(ClientModConstructor constructor, String modId) {
        List<DynamicModelBakingContext> modelBakingListeners = Lists.newArrayList();
        constructor.onRegisterModelBakingListeners(modelBakingListeners::add);
        if (modelBakingListeners.isEmpty()) return;
        ModelEvents.MODIFY_BAKING_RESULT.register((models, modelBakery) -> {
            for (DynamicModelBakingContext listener : modelBakingListeners) {
                if (!(listener instanceof DynamicModelBakingContext.ModifyBakingResult modifyBakingResult))
                    continue;
                try {
                    modifyBakingResult.onModifyBakingResult(models, modelBakery);
                } catch (Exception e) {
                    PuzzlesLib.LOGGER.error("Unable to execute additional resource pack model processing provided by {}", modId, e);
                }
            }
        });
        ModelEvents.BAKING_COMPLETED.register((modelManager, models, modelBakery) -> {
            for (DynamicModelBakingContext listener : modelBakingListeners) {
                if (!(listener instanceof DynamicModelBakingContext.BakingCompleted bakingCompleted)) continue;
                try {
                    bakingCompleted.onBakingCompleted(modelManager, models, modelBakery);
                } catch (Exception e) {
                    PuzzlesLib.LOGGER.error("Unable to execute additional resource pack model processing provided by {}", modId, e);
                }
            }
        });
    }

    private static void registerBuiltinModelItemRenderers(ClientModConstructor constructor, String modId) {
        BuiltinModelItemRendererContextFabricImpl context = new BuiltinModelItemRendererContextFabricImpl(modId);
        constructor.onRegisterBuiltinModelItemRenderers(context);
        context.apply();
    }
}
