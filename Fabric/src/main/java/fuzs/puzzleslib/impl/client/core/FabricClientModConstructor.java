package fuzs.puzzleslib.impl.client.core;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.core.v1.context.DynamicBakingCompletedContext;
import fuzs.puzzleslib.api.client.core.v1.context.DynamicModifyBakingResultContext;
import fuzs.puzzleslib.api.client.event.v2.ModelEvents;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.core.context.*;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.List;
import java.util.function.Consumer;

public final class FabricClientModConstructor {

    private FabricClientModConstructor() {

    }

    public static void construct(ClientModConstructor constructor, String modId, ContentRegistrationFlags... contentRegistrations) {
        constructor.onConstructMod();
        constructor.onClientSetup(Runnable::run);
        constructor.onRegisterEntityRenderers(new EntityRenderersContextFabricImpl());
        constructor.onRegisterBlockEntityRenderers(new BlockEntityRenderersContextFabricImpl());
        constructor.onRegisterClientTooltipComponents(new ClientTooltipComponentsContextFabricImpl());
        constructor.onRegisterParticleProviders(new ParticleProvidersContextFabricImpl());
        constructor.onRegisterLayerDefinitions(new LayerDefinitionsContextFabricImpl());
        constructor.onRegisterSearchTrees(new SearchRegistryContextFabricImpl());
        registerModelBakingListeners(constructor::onModifyBakingResult, constructor::onBakingCompleted, modId);
        constructor.onRegisterAdditionalModels(new AdditionalModelsContextFabricImpl());
        constructor.onRegisterItemModelProperties(new ItemModelPropertiesContextFabricImpl());
        constructor.onRegisterEntitySpectatorShaders(new EntitySpectatorShaderContextFabricImpl());
        registerBuiltinModelItemRenderers(constructor::onRegisterBuiltinModelItemRenderers, modId);
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

    private static void registerModelBakingListeners(Consumer<DynamicModifyBakingResultContext> consumer1, Consumer<DynamicBakingCompletedContext> consumer2, String modId) {
        ModelEvents.MODIFY_BAKING_RESULT.register((models, modelBakery) -> {
            try {
                consumer1.accept(new DynamicModifyBakingResultContext(models, modelBakery));
            } catch (Exception e) {
                PuzzlesLib.LOGGER.error("Unable to execute additional resource pack model processing during modify baking result phase provided by {}", modId, e);
            }
        });
        ModelEvents.BAKING_COMPLETED.register((modelManager, models, modelBakery) -> {
            try {
                consumer2.accept(new DynamicBakingCompletedContext(modelManager, models, modelBakery));
            } catch (Exception e) {
                PuzzlesLib.LOGGER.error("Unable to execute additional resource pack model processing during baking completed phase provided by {}", modId, e);
            }
        });
    }

    private static void registerBuiltinModelItemRenderers(Consumer<BuiltinModelItemRendererContext> consumer, String modId) {
        List<ResourceManagerReloadListener> listeners = Lists.newArrayList();
        BuiltinModelItemRendererContextFabricImpl context = new BuiltinModelItemRendererContextFabricImpl(listeners);
        consumer.accept(context);
        if (listeners.isEmpty()) return;
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricResourceReloadListener(modId, "built_in_model_item_renderers", (ResourceManagerReloadListener) (ResourceManager resourceManager) -> {
            for (ResourceManagerReloadListener listener : listeners) {
                try {
                    listener.onResourceManagerReload(resourceManager);
                } catch (Exception e) {
                    PuzzlesLib.LOGGER.error("Unable to execute dynamic built-in model item renderers reload provided by {}", modId, e);
                }
            }
        }));
    }
}
