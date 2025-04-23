package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

import java.util.function.UnaryOperator;

/**
 * Register new {@link LayeredDraw.Layer Layers} to be drawn as part of the {@link net.minecraft.client.gui.Gui}.
 */
public interface GuiLayersContext {
    ResourceLocation CAMERA_OVERLAYS = ResourceLocationHelper.withDefaultNamespace("camera_overlays");
    ResourceLocation CROSSHAIR = ResourceLocationHelper.withDefaultNamespace("crosshair");
    ResourceLocation HOTBAR = ResourceLocationHelper.withDefaultNamespace("hotbar");
    ResourceLocation JUMP_METER = ResourceLocationHelper.withDefaultNamespace("jump_meter");
    ResourceLocation EXPERIENCE_BAR = ResourceLocationHelper.withDefaultNamespace("experience_bar");
    ResourceLocation PLAYER_HEALTH = ResourceLocationHelper.withDefaultNamespace("player_health");
    ResourceLocation ARMOR_LEVEL = ResourceLocationHelper.withDefaultNamespace("armor_level");
    ResourceLocation FOOD_LEVEL = ResourceLocationHelper.withDefaultNamespace("food_level");
    ResourceLocation VEHICLE_HEALTH = ResourceLocationHelper.withDefaultNamespace("vehicle_health");
    ResourceLocation AIR_LEVEL = ResourceLocationHelper.withDefaultNamespace("air_level");
    ResourceLocation SELECTED_ITEM_NAME = ResourceLocationHelper.withDefaultNamespace("selected_item_name");
    ResourceLocation EXPERIENCE_LEVEL = ResourceLocationHelper.withDefaultNamespace("experience_level");
    ResourceLocation STATUS_EFFECTS = ResourceLocationHelper.withDefaultNamespace("status_effects");
    ResourceLocation BOSS_BAR = ResourceLocationHelper.withDefaultNamespace("boss_bar");
    ResourceLocation SLEEP_OVERLAY = ResourceLocationHelper.withDefaultNamespace("sleep_overlay");
    ResourceLocation DEMO_TIMER = ResourceLocationHelper.withDefaultNamespace("demo_timer");
    ResourceLocation DEBUG_OVERLAY = ResourceLocationHelper.withDefaultNamespace("debug_overlay");
    ResourceLocation SCOREBOARD = ResourceLocationHelper.withDefaultNamespace("scoreboard");
    ResourceLocation OVERLAY_MESSAGE = ResourceLocationHelper.withDefaultNamespace("overlay_message");
    ResourceLocation TITLE = ResourceLocationHelper.withDefaultNamespace("title");
    ResourceLocation CHAT = ResourceLocationHelper.withDefaultNamespace("chat");
    ResourceLocation PLAYER_LIST = ResourceLocationHelper.withDefaultNamespace("player_list");
    ResourceLocation SUBTITLES = ResourceLocationHelper.withDefaultNamespace("subtitles");
    /**
     * An empty gui layer factory for removing existing vanilla gui layers from rendering via
     * {@link #replaceGuiLayer(ResourceLocation, UnaryOperator)}.
     */
    UnaryOperator<LayeredDraw.Layer> EMPTY = (LayeredDraw.Layer layer) -> {
        return (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
            // NO-OP
        };
    };

    /**
     * Register a new gui layer rendered after all existing layers.
     *
     * @param resourceLocation the gui layer resource location
     * @param guiLayer         the gui layer
     */
    void registerGuiLayer(ResourceLocation resourceLocation, LayeredDraw.Layer guiLayer);

    /**
     * Register a new gui layer rendered before or after an existing vanilla gui layer.
     * <p>
     * The ordering depends on the order in which both resource location arguments are passed.
     *
     * @param resourceLocation      the gui layer resource location, either for the new layer or for the existing
     *                              vanilla layer
     * @param otherResourceLocation the other gui layer resource location, either for the new layer or for the existing
     *                              vanilla layer
     * @param guiLayer              the gui layer
     */
    void registerGuiLayer(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, LayeredDraw.Layer guiLayer);

    /**
     * Replace an existing vanilla gui layer. Replacing custom layers is not supported.
     *
     * @param resourceLocation the vanilla gui layer resource location
     * @param guiLayerFactory  the gui layer factory, receiving the existing layer
     */
    void replaceGuiLayer(ResourceLocation resourceLocation, UnaryOperator<LayeredDraw.Layer> guiLayerFactory);
}
