package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.core.v1.context.ReloadListenersContext;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
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

/**
 * Adds listeners to the client (resource packs) resource manager to reload together with other resources.
 */
public interface ResourcePackReloadListenersContext extends ReloadListenersContext {
    /**
     * The {@link LanguageManager} reload listener.
     */
    ResourceLocation LANGUAGES = ResourceLocationHelper.withDefaultNamespace("languages");
    /**
     * The {@link TextureManager} reload listener.
     */
    ResourceLocation TEXTURES = ResourceLocationHelper.withDefaultNamespace("textures");
    /**
     * The {@link ShaderManager} reload listener.
     */
    ResourceLocation SHADERS = ResourceLocationHelper.withDefaultNamespace("shaders");
    /**
     * The {@link SoundManager} reload listener.
     */
    ResourceLocation SOUNDS = ResourceLocationHelper.withDefaultNamespace("sounds");
    /**
     * The {@link SplashManager} reload listener.
     */
    ResourceLocation SPLASH_TEXTS = ResourceLocationHelper.withDefaultNamespace("splash_texts");
    /**
     * The {@link AtlasManager} reload listener.
     */
    ResourceLocation ATLASES = ResourceLocationHelper.withDefaultNamespace("atlases");
    /**
     * The {@link FontManager} reload listener.
     */
    ResourceLocation FONTS = ResourceLocationHelper.withDefaultNamespace("fonts");
    /**
     * The {@link GrassColorReloadListener} reload listener.
     */
    ResourceLocation GRASS_COLORMAP = ResourceLocationHelper.withDefaultNamespace("grass_colormap");
    /**
     * The {@link FoliageColorReloadListener} reload listener.
     */
    ResourceLocation FOLIAGE_COLORMAP = ResourceLocationHelper.withDefaultNamespace("foliage_colormap");
    /**
     * The {@link DryFoliageColorReloadListener} reload listener.
     */
    ResourceLocation DRY_FOLIAGE_COLORMAP = ResourceLocationHelper.withDefaultNamespace("dry_foliage_colormap");
    /**
     * The {@link ModelManager} reload listener.
     */
    ResourceLocation MODELS = ResourceLocationHelper.withDefaultNamespace("models");
    /**
     * The {@link EquipmentAssetManager} reload listener.
     */
    ResourceLocation EQUIPMENT_ASSETS = ResourceLocationHelper.withDefaultNamespace("equipment_assets");
    /**
     * The {@link BlockRenderDispatcher} reload listener.
     */
    ResourceLocation BLOCK_RENDERER = ResourceLocationHelper.withDefaultNamespace("block_renderer");
    /**
     * The {@link EntityRenderDispatcher} reload listener.
     */
    ResourceLocation ENTITY_RENDERER = ResourceLocationHelper.withDefaultNamespace("entity_renderer");
    /**
     * The {@link BlockEntityRenderDispatcher} reload listener.
     */
    ResourceLocation BLOCK_ENTITY_RENDERER = ResourceLocationHelper.withDefaultNamespace("block_entity_renderer");
    /**
     * The {@link ParticleResources} reload listener.
     */
    ResourceLocation PARTICLE_RESOURCES = ResourceLocationHelper.withDefaultNamespace("particle_resources");
    /**
     * The {@link WaypointStyleManager} reload listener.
     */
    ResourceLocation WAYPOINT_STYLES = ResourceLocationHelper.withDefaultNamespace("waypoint_styles");
    /**
     * The {@link LevelRenderer} reload listener.
     */
    ResourceLocation LEVEL_RENDERER = ResourceLocationHelper.withDefaultNamespace("level_renderer");
    /**
     * The {@link CloudRenderer} reload listener.
     */
    ResourceLocation CLOUD_RENDERER = ResourceLocationHelper.withDefaultNamespace("cloud_renderer");
    /**
     * The {@link GpuWarnlistManager} reload listener.
     */
    ResourceLocation GPU_WARNLIST = ResourceLocationHelper.withDefaultNamespace("gpu_warnlist");
    /**
     * The {@link PeriodicNotificationManager} reload listener.
     */
    ResourceLocation REGIONAL_COMPLIANCES = ResourceLocationHelper.withDefaultNamespace("regional_compliances");
}
