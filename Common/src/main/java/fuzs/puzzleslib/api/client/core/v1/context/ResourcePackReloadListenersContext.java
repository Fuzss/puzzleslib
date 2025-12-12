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
import net.minecraft.resources.Identifier;
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
    Identifier LANGUAGES = Identifier.withDefaultNamespace("languages");
    /**
     * The {@link TextureManager} reload listener.
     */
    Identifier TEXTURES = Identifier.withDefaultNamespace("textures");
    /**
     * The {@link ShaderManager} reload listener.
     */
    Identifier SHADERS = Identifier.withDefaultNamespace("shaders");
    /**
     * The {@link SoundManager} reload listener.
     */
    Identifier SOUNDS = Identifier.withDefaultNamespace("sounds");
    /**
     * The {@link SplashManager} reload listener.
     */
    Identifier SPLASH_TEXTS = Identifier.withDefaultNamespace("splash_texts");
    /**
     * The {@link AtlasManager} reload listener.
     */
    Identifier ATLASES = Identifier.withDefaultNamespace("atlases");
    /**
     * The {@link FontManager} reload listener.
     */
    Identifier FONTS = Identifier.withDefaultNamespace("fonts");
    /**
     * The {@link GrassColorReloadListener} reload listener.
     */
    Identifier GRASS_COLORMAP = Identifier.withDefaultNamespace("grass_colormap");
    /**
     * The {@link FoliageColorReloadListener} reload listener.
     */
    Identifier FOLIAGE_COLORMAP = Identifier.withDefaultNamespace("foliage_colormap");
    /**
     * The {@link DryFoliageColorReloadListener} reload listener.
     */
    Identifier DRY_FOLIAGE_COLORMAP = Identifier.withDefaultNamespace("dry_foliage_colormap");
    /**
     * The {@link ModelManager} reload listener.
     */
    Identifier MODELS = Identifier.withDefaultNamespace("models");
    /**
     * The {@link EquipmentAssetManager} reload listener.
     */
    Identifier EQUIPMENT_ASSETS = Identifier.withDefaultNamespace("equipment_assets");
    /**
     * The {@link BlockRenderDispatcher} reload listener.
     */
    Identifier BLOCK_RENDERER = Identifier.withDefaultNamespace("block_renderer");
    /**
     * The {@link EntityRenderDispatcher} reload listener.
     */
    Identifier ENTITY_RENDERER = Identifier.withDefaultNamespace("entity_renderer");
    /**
     * The {@link BlockEntityRenderDispatcher} reload listener.
     */
    Identifier BLOCK_ENTITY_RENDERER = Identifier.withDefaultNamespace("block_entity_renderer");
    /**
     * The {@link ParticleResources} reload listener.
     */
    Identifier PARTICLE_RESOURCES = Identifier.withDefaultNamespace("particle_resources");
    /**
     * The {@link WaypointStyleManager} reload listener.
     */
    Identifier WAYPOINT_STYLES = Identifier.withDefaultNamespace("waypoint_styles");
    /**
     * The {@link LevelRenderer} reload listener.
     */
    Identifier LEVEL_RENDERER = Identifier.withDefaultNamespace("level_renderer");
    /**
     * The {@link CloudRenderer} reload listener.
     */
    Identifier CLOUD_RENDERER = Identifier.withDefaultNamespace("cloud_renderer");
    /**
     * The {@link GpuWarnlistManager} reload listener.
     */
    Identifier GPU_WARNLIST = Identifier.withDefaultNamespace("gpu_warnlist");
    /**
     * The {@link PeriodicNotificationManager} reload listener.
     */
    Identifier REGIONAL_COMPLIANCES = Identifier.withDefaultNamespace("regional_compliances");

    /**
     * Register a {@link PreparableReloadListener}.
     *
     * @param identifier the reload listener identifier
     * @param reloadListener   the reload listener to add
     */
    void registerReloadListener(Identifier identifier, PreparableReloadListener reloadListener);

    /**
     * Register a {@link PreparableReloadListener}.
     *
     * @param identifier the reload listener identifier
     * @param otherIdentifier  the other reload listener identifier, either for the new listener or for the
     *                         existing vanilla listener
     * @param reloadListener   the reload listener to add
     */
    void registerReloadListener(Identifier identifier, Identifier otherIdentifier, PreparableReloadListener reloadListener);

    /**
     * Register a {@link ResourceManagerReloadListener}.
     *
     * @param identifier the reload listener identifier
     * @param reloadListener   the reload listener to add
     */
    default void registerReloadListener(Identifier identifier, ResourceManagerReloadListener reloadListener) {
        this.registerReloadListener(identifier, (PreparableReloadListener) reloadListener);
    }

    /**
     * Register a {@link ResourceManagerReloadListener}.
     *
     * @param identifier the reload listener identifier, either for the new listener or for the existing
     *                         vanilla listener
     * @param otherIdentifier  the other reload listener identifier, either for the new listener or for the
     *                         existing vanilla listener
     * @param reloadListener   the reload listener to add
     */
    default void registerReloadListener(Identifier identifier, Identifier otherIdentifier, ResourceManagerReloadListener reloadListener) {
        this.registerReloadListener(identifier, otherIdentifier, (PreparableReloadListener) reloadListener);
    }

    /**
     * Register a {@link SimplePreparableReloadListener}.
     *
     * @param identifier the reload listener identifier, either for the new listener or for the existing
     *                         vanilla listener
     * @param reloadListener   the reload listener to add
     */
    default <T> void registerReloadListener(Identifier identifier, SimplePreparableReloadListener<T> reloadListener) {
        this.registerReloadListener(identifier, (PreparableReloadListener) reloadListener);
    }

    /**
     * Register a {@link SimplePreparableReloadListener}.
     *
     * @param identifier the reload listener identifier, either for the new listener or for the existing
     *                         vanilla listener
     * @param otherIdentifier  the other reload listener identifier, either for the new listener or for the
     *                         existing vanilla listener
     * @param reloadListener   the reload listener to add
     */
    default <T> void registerReloadListener(Identifier identifier, Identifier otherIdentifier, SimplePreparableReloadListener<T> reloadListener) {
        this.registerReloadListener(identifier, otherIdentifier, (PreparableReloadListener) reloadListener);
    }
}
