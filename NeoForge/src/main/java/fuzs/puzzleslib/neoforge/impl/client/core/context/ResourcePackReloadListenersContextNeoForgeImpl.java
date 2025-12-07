package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.client.core.v1.context.ResourcePackReloadListenersContext;
import fuzs.puzzleslib.neoforge.impl.core.context.DataPackReloadListenersContextNeoForgeImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.resources.VanillaClientListeners;

import java.util.Map;
import java.util.Objects;

public record ResourcePackReloadListenersContextNeoForgeImpl(AddClientReloadListenersEvent event) implements ResourcePackReloadListenersContext {
    private static final Map<ResourceLocation, ResourceLocation> VANILLA_CLIENT_RELOAD_LISTENERS = ImmutableMap.<ResourceLocation, ResourceLocation>builder()
            .put(LANGUAGES, VanillaClientListeners.LANGUAGE)
            .put(TEXTURES, VanillaClientListeners.TEXTURES)
            .put(SHADERS, VanillaClientListeners.SHADERS)
            .put(SOUNDS, VanillaClientListeners.SOUNDS)
            .put(SPLASH_TEXTS, VanillaClientListeners.SPLASHES)
            .put(ATLASES, VanillaClientListeners.ATLASES)
            .put(FONTS, VanillaClientListeners.FONTS)
            .put(GRASS_COLORMAP, VanillaClientListeners.GRASS_COLOR)
            .put(FOLIAGE_COLORMAP, VanillaClientListeners.FOLIAGE_COLOR)
            .put(DRY_FOLIAGE_COLORMAP, VanillaClientListeners.DRY_FOLIAGE_COLOR)
            .put(MODELS, VanillaClientListeners.MODELS)
            .put(EQUIPMENT_ASSETS, VanillaClientListeners.EQUIPMENT_ASSETS)
            .put(BLOCK_RENDERER, VanillaClientListeners.BLOCK_RENDERER)
            .put(ENTITY_RENDERER, VanillaClientListeners.ENTITY_RENDERER)
            .put(BLOCK_ENTITY_RENDERER, VanillaClientListeners.BLOCK_ENTITY_RENDERER)
            .put(PARTICLE_RESOURCES, VanillaClientListeners.PARTICLE_RESOURCES)
            .put(WAYPOINT_STYLES, VanillaClientListeners.WAYPOINT_STYLES)
            .put(LEVEL_RENDERER, VanillaClientListeners.LEVEL_RENDERER)
            .put(CLOUD_RENDERER, VanillaClientListeners.CLOUD_RENDERER)
            .put(GPU_WARNLIST, VanillaClientListeners.GPU_WARNLIST)
            .put(REGIONAL_COMPLIANCES, VanillaClientListeners.REGIONAL_COMPLIANCES)
            .build();

    @Override
    public void registerReloadListener(ResourceLocation resourceLocation, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        this.event.addListener(resourceLocation, reloadListener);
    }

    @Override
    public void registerReloadListener(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(otherResourceLocation, "other resource location is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        DataPackReloadListenersContextNeoForgeImpl.registerReloadListener(this.event,
                resourceLocation,
                otherResourceLocation,
                reloadListener,
                VANILLA_CLIENT_RELOAD_LISTENERS);
    }
}
