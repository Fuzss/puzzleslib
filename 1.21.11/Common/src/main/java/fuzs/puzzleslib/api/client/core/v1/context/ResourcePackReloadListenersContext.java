package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.PeriodicNotificationManager;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

/**
 * Adds listeners to the client (resource packs) resource manager to reload together with other resources.
 */
public interface ResourcePackReloadListenersContext {
    /**
     * The {@link LanguageManager} reload listener.
     */
    ResourceLocation LANGUAGES = ResourceLocation.withDefaultNamespace("languages");
    /**
     * The {@link TextureManager} reload listener.
     */
    ResourceLocation TEXTURES = ResourceLocation.withDefaultNamespace("textures");
    /**
     * The {@link ShaderManager} reload listener.
     */
    ResourceLocation SHADERS = ResourceLocation.withDefaultNamespace("shaders");
    /**
     * The {@link SoundManager} reload listener.
     */
    ResourceLocation SOUNDS = ResourceLocation.withDefaultNamespace("sounds");
    /**
     * The {@link SplashManager} reload listener.
     */
    ResourceLocation SPLASH_TEXTS = ResourceLocation.withDefaultNamespace("splash_texts");
    /**
     * The {@link AtlasManager} reload listener.
     */
    ResourceLocation ATLASES = ResourceLocation.withDefaultNamespace("atlases");
    /**
     * The {@link FontManager} reload listener.
     */
    ResourceLocation FONTS = ResourceLocation.withDefaultNamespace("fonts");
    /**
     * The {@link GrassColorReloadListener} reload listener.
     */
    ResourceLocation GRASS_COLORMAP = ResourceLocation.withDefaultNamespace("grass_colormap");
    /**
     * The {@link FoliageColorReloadListener} reload listener.
     */
    ResourceLocation FOLIAGE_COLORMAP = ResourceLocation.withDefaultNamespace("foliage_colormap");
    /**
     * The {@link DryFoliageColorReloadListener} reload listener.
     */
    ResourceLocation DRY_FOLIAGE_COLORMAP = ResourceLocation.withDefaultNamespace("dry_foliage_colormap");
    /**
     * The {@link ModelManager} reload listener.
     */
    ResourceLocation MODELS = ResourceLocation.withDefaultNamespace("models");
    /**
     * The {@link EquipmentAssetManager} reload listener.
     */
    ResourceLocation EQUIPMENT_ASSETS = ResourceLocation.withDefaultNamespace("equipment_assets");
    /**
     * The {@link BlockRenderDispatcher} reload listener.
     */
    ResourceLocation BLOCK_RENDERER = ResourceLocation.withDefaultNamespace("block_renderer");
    /**
     * The {@link EntityRenderDispatcher} reload listener.
     */
    ResourceLocation ENTITY_RENDERER = ResourceLocation.withDefaultNamespace("entity_renderer");
    /**
     * The {@link BlockEntityRenderDispatcher} reload listener.
     */
    ResourceLocation BLOCK_ENTITY_RENDERER = ResourceLocation.withDefaultNamespace("block_entity_renderer");
    /**
     * The {@link ParticleResources} reload listener.
     */
    ResourceLocation PARTICLE_RESOURCES = ResourceLocation.withDefaultNamespace("particle_resources");
    /**
     * The {@link WaypointStyleManager} reload listener.
     */
    ResourceLocation WAYPOINT_STYLES = ResourceLocation.withDefaultNamespace("waypoint_styles");
    /**
     * The {@link LevelRenderer} reload listener.
     */
    ResourceLocation LEVEL_RENDERER = ResourceLocation.withDefaultNamespace("level_renderer");
    /**
     * The {@link CloudRenderer} reload listener.
     */
    ResourceLocation CLOUD_RENDERER = ResourceLocation.withDefaultNamespace("cloud_renderer");
    /**
     * The {@link GpuWarnlistManager} reload listener.
     */
    ResourceLocation GPU_WARNLIST = ResourceLocation.withDefaultNamespace("gpu_warnlist");
    /**
     * The {@link PeriodicNotificationManager} reload listener.
     */
    ResourceLocation REGIONAL_COMPLIANCES = ResourceLocation.withDefaultNamespace("regional_compliances");

    /**
     * Register a {@link PreparableReloadListener}.
     *
     * @param resourceLocation the reload listener resource location
     * @param reloadListener   the reload listener to add
     */
    void registerReloadListener(ResourceLocation resourceLocation, PreparableReloadListener reloadListener);

    /**
     * Register a {@link PreparableReloadListener}.
     *
     * @param resourceLocation      the reload listener resource location
     * @param otherResourceLocation the other reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param reloadListener        the reload listener to add
     */
    void registerReloadListener(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, PreparableReloadListener reloadListener);

    /**
     * Register a {@link ResourceManagerReloadListener}.
     *
     * @param resourceLocation the reload listener resource location
     * @param reloadListener   the reload listener to add
     */
    default void registerReloadListener(ResourceLocation resourceLocation, ResourceManagerReloadListener reloadListener) {
        this.registerReloadListener(resourceLocation, (PreparableReloadListener) reloadListener);
    }

    /**
     * Register a {@link ResourceManagerReloadListener}.
     *
     * @param resourceLocation      the reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param otherResourceLocation the other reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param reloadListener        the reload listener to add
     */
    default void registerReloadListener(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, ResourceManagerReloadListener reloadListener) {
        this.registerReloadListener(resourceLocation, otherResourceLocation, (PreparableReloadListener) reloadListener);
    }

    /**
     * Register a {@link SimplePreparableReloadListener}.
     *
     * @param resourceLocation the reload listener resource location, either for the new listener or for the existing
     *                         vanilla listener
     * @param reloadListener   the reload listener to add
     */
    default <T> void registerReloadListener(ResourceLocation resourceLocation, SimplePreparableReloadListener<T> reloadListener) {
        this.registerReloadListener(resourceLocation, (PreparableReloadListener) reloadListener);
    }

    /**
     * Register a {@link SimplePreparableReloadListener}.
     *
     * @param resourceLocation      the reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param otherResourceLocation the other reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param reloadListener        the reload listener to add
     */
    default <T> void registerReloadListener(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, SimplePreparableReloadListener<T> reloadListener) {
        this.registerReloadListener(resourceLocation, otherResourceLocation, (PreparableReloadListener) reloadListener);
    }
}
