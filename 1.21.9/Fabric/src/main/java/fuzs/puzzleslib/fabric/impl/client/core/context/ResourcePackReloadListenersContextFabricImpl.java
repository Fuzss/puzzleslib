package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.client.core.v1.context.ResourcePackReloadListenersContext;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Map;
import java.util.Objects;

public final class ResourcePackReloadListenersContextFabricImpl implements ResourcePackReloadListenersContext {
    private static final Map<ResourceLocation, ResourceLocation> VANILLA_CLIENT_RELOAD_LISTENERS = ImmutableMap.<ResourceLocation, ResourceLocation>builder()
            .put(LANGUAGES, ResourceReloaderKeys.Client.LANGUAGES)
            .put(TEXTURES, ResourceReloaderKeys.Client.TEXTURES)
            .put(SHADERS, ResourceReloaderKeys.Client.SHADERS)
            .put(SOUNDS, ResourceReloaderKeys.Client.SOUNDS)
            .put(SPLASH_TEXTS, ResourceReloaderKeys.Client.SPLASH_TEXTS)
            .put(ATLASES, ResourceReloaderKeys.Client.ATLAS)
            .put(FONTS, ResourceReloaderKeys.Client.FONTS)
            .put(GRASS_COLORMAP, ResourceReloaderKeys.Client.GRASS_COLORMAP)
            .put(FOLIAGE_COLORMAP, ResourceReloaderKeys.Client.FOLIAGE_COLORMAP)
            .put(DRY_FOLIAGE_COLORMAP, ResourceReloaderKeys.Client.DRY_FOLIAGE_COLORMAP)
            .put(MODELS, ResourceReloaderKeys.Client.MODELS)
            .put(EQUIPMENT_ASSETS, ResourceReloaderKeys.Client.EQUIPMENT_MODELS)
            .put(BLOCK_RENDERER, ResourceReloaderKeys.Client.BLOCK_RENDER_MANAGER)
            .put(ENTITY_RENDERER, ResourceReloaderKeys.Client.ENTITY_RENDERERS)
            .put(BLOCK_ENTITY_RENDERER, ResourceReloaderKeys.Client.BLOCK_ENTITY_RENDERERS)
            .put(PARTICLE_RESOURCES, ResourceReloaderKeys.Client.PARTICLES)
            .put(WAYPOINT_STYLES, ResourceReloaderKeys.Client.WAYPOINT_STYLE_ASSETS)
            .put(LEVEL_RENDERER, ResourceReloaderKeys.BEFORE_VANILLA)
            .put(CLOUD_RENDERER, ResourceReloaderKeys.Client.CLOUD_CELLS)
            .put(GPU_WARNLIST, ResourceReloaderKeys.BEFORE_VANILLA)
            .put(REGIONAL_COMPLIANCES, ResourceReloaderKeys.BEFORE_VANILLA)
            .build();

    @Override
    public void registerReloadListener(ResourceLocation resourceLocation, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(resourceLocation, "id is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(resourceLocation, reloadListener);
    }

    @Override
    public void registerReloadListener(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(otherResourceLocation, "other resource location is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        if (VANILLA_CLIENT_RELOAD_LISTENERS.containsKey(resourceLocation)) {
            ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(otherResourceLocation, reloadListener);
            ResourceLoader.get(PackType.CLIENT_RESOURCES)
                    .addReloaderOrdering(VANILLA_CLIENT_RELOAD_LISTENERS.get(resourceLocation), otherResourceLocation);
        } else if (VANILLA_CLIENT_RELOAD_LISTENERS.containsKey(otherResourceLocation)) {
            ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(resourceLocation, reloadListener);
            ResourceLoader.get(PackType.CLIENT_RESOURCES)
                    .addReloaderOrdering(resourceLocation, VANILLA_CLIENT_RELOAD_LISTENERS.get(otherResourceLocation));
        } else {
            throw new RuntimeException("Unknown reload listeners: " + resourceLocation + ", " + otherResourceLocation);
        }
    }
}
