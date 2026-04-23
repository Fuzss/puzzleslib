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
            .put(GRASS_COLOR, ResourceReloaderKeys.Client.GRASS_COLOR)
            .put(FOLIAGE_COLOR, ResourceReloaderKeys.Client.FOLIAGE_COLOR)
            .put(DRY_FOLIAGE_COLOR, ResourceReloaderKeys.Client.DRY_FOLIAGE_COLOR)
            .put(MODELS, ResourceReloaderKeys.Client.MODELS)
            .put(EQUIPMENT_ASSETS, ResourceReloaderKeys.Client.EQUIPMENT_ASSETS)
            .put(ENTITY_RENDERER, ResourceReloaderKeys.Client.ENTITY_RENDER_DISPATCHER)
            .put(BLOCK_ENTITY_RENDERER, ResourceReloaderKeys.Client.BLOCK_ENTITY_RENDER_DISPATCHER)
            .put(PARTICLE_RESOURCES, ResourceReloaderKeys.Client.PARTICLES)
            .put(WAYPOINT_STYLES, ResourceReloaderKeys.Client.WAYPOINT_STYLE)
            .put(LEVEL_RENDERER, ResourceReloaderKeys.BEFORE_VANILLA)
            .put(CLOUD_RENDERER, ResourceReloaderKeys.Client.CLOUD_RENDERER)
            .put(GPU_WARNLIST, ResourceReloaderKeys.BEFORE_VANILLA)
            .put(REGIONAL_COMPLIANCES, ResourceReloaderKeys.BEFORE_VANILLA)
            .build();

    @Override
    public void registerReloadListener(Identifier identifier, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(identifier, "id is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(identifier, reloadListener);
    }

    @Override
    public void registerReloadListener(Identifier identifier, Identifier otherResourceLocation, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(otherResourceLocation, "other identifier is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        if (VANILLA_CLIENT_RELOAD_LISTENERS.containsKey(identifier)) {
            ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(otherResourceLocation, reloadListener);
            ResourceLoader.get(PackType.CLIENT_RESOURCES)
                    .addListenerOrdering(VANILLA_CLIENT_RELOAD_LISTENERS.get(identifier), otherResourceLocation);
        } else if (VANILLA_CLIENT_RELOAD_LISTENERS.containsKey(otherResourceLocation)) {
            ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(identifier, reloadListener);
            ResourceLoader.get(PackType.CLIENT_RESOURCES)
                    .addListenerOrdering(identifier, VANILLA_CLIENT_RELOAD_LISTENERS.get(otherResourceLocation));
        } else {
            throw new RuntimeException("Unknown reload listeners: " + identifier + ", " + otherResourceLocation);
        }
    }
}
