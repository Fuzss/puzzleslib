package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.client.core.v1.context.ResourcePackReloadListenersContext;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Map;
import java.util.Objects;

public final class ResourcePackReloadListenersContextFabricImpl implements ResourcePackReloadListenersContext {
    private static final Map<Identifier, Identifier> VANILLA_CLIENT_RELOAD_LISTENERS = ImmutableMap.<Identifier, Identifier>builder()
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
    public void registerReloadListener(Identifier identifier, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(identifier, "id is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(identifier, reloadListener);
    }

    @Override
    public void registerReloadListener(Identifier identifier, Identifier otherResourceLocation, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(otherResourceLocation, "other identifier is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        if (VANILLA_CLIENT_RELOAD_LISTENERS.containsKey(identifier)) {
            ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(otherResourceLocation, reloadListener);
            ResourceLoader.get(PackType.CLIENT_RESOURCES)
                    .addReloaderOrdering(VANILLA_CLIENT_RELOAD_LISTENERS.get(identifier), otherResourceLocation);
        } else if (VANILLA_CLIENT_RELOAD_LISTENERS.containsKey(otherResourceLocation)) {
            ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(identifier, reloadListener);
            ResourceLoader.get(PackType.CLIENT_RESOURCES)
                    .addReloaderOrdering(identifier, VANILLA_CLIENT_RELOAD_LISTENERS.get(otherResourceLocation));
        } else {
            throw new RuntimeException("Unknown reload listeners: " + identifier + ", " + otherResourceLocation);
        }
    }
}
