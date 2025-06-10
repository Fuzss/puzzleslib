package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.function.UnaryOperator;

/**
 * Register new {@link Layer Layers} to be drawn as part of the {@link net.minecraft.client.gui.Gui}.
 */
public interface GuiLayersContext {
    /**
     * The layer for rendering camera-related overlays like pumpkin blur.
     */
    ResourceLocation CAMERA_OVERLAYS = ResourceLocationHelper.withDefaultNamespace("camera_overlays");
    /**
     * The layer for rendering the crosshair.
     */
    ResourceLocation CROSSHAIR = ResourceLocationHelper.withDefaultNamespace("crosshair");
    /**
     * The layer for rendering the hotbar.
     */
    ResourceLocation HOTBAR = ResourceLocationHelper.withDefaultNamespace("hotbar");
    /**
     * The layer for rendering the info bars, like the experience bar, the locator bar, or the jump meter (e.g. for
     * horses).
     */
    ResourceLocation INFO_BAR = ResourceLocationHelper.withDefaultNamespace("info_bar");
    /**
     * The layer for rendering the player's health hearts.
     */
    ResourceLocation PLAYER_HEALTH = ResourceLocationHelper.withDefaultNamespace("player_health");
    /**
     * The layer for rendering the player's armor level.
     */
    ResourceLocation ARMOR_LEVEL = ResourceLocationHelper.withDefaultNamespace("armor_level");
    /**
     * The layer for rendering the player's food level.
     */
    ResourceLocation FOOD_LEVEL = ResourceLocationHelper.withDefaultNamespace("food_level");
    /**
     * The layer for rendering the health of the player's vehicle.
     */
    ResourceLocation VEHICLE_HEALTH = ResourceLocationHelper.withDefaultNamespace("vehicle_health");
    /**
     * The layer for rendering the player's air level when submerged.
     */
    ResourceLocation AIR_LEVEL = ResourceLocationHelper.withDefaultNamespace("air_level");
    /**
     * The layer for rendering the name of the selected item.
     */
    ResourceLocation HELD_ITEM_TOOLTIP = ResourceLocationHelper.withDefaultNamespace("held_item_tooltip");
    /**
     * The layer for rendering the player's experience level number.
     */
    ResourceLocation EXPERIENCE_LEVEL = ResourceLocationHelper.withDefaultNamespace("experience_level");
    /**
     * The layer for rendering the selected spectator menu action.
     */
    ResourceLocation SPECTATOR_TOOLTIP = ResourceLocationHelper.withDefaultNamespace("spectator_tooltip");
    /**
     * The layer for rendering status effect icons.
     */
    ResourceLocation STATUS_EFFECTS = ResourceLocationHelper.withDefaultNamespace("status_effects");
    /**
     * The layer for rendering boss bars.
     */
    ResourceLocation BOSS_BAR = ResourceLocationHelper.withDefaultNamespace("boss_bar");
    /**
     * The layer for rendering the sleep overlay.
     */
    ResourceLocation SLEEP_OVERLAY = ResourceLocationHelper.withDefaultNamespace("sleep_overlay");
    /**
     * The layer for rendering the demo mode timer.
     */
    ResourceLocation DEMO_TIMER = ResourceLocationHelper.withDefaultNamespace("demo_timer");
    /**
     * The layer for rendering the debug overlay (F3 screen).
     */
    ResourceLocation DEBUG_OVERLAY = ResourceLocationHelper.withDefaultNamespace("debug_overlay");
    /**
     * The layer for rendering the scoreboard.
     */
    ResourceLocation SCOREBOARD = ResourceLocationHelper.withDefaultNamespace("scoreboard");
    /**
     * The layer for rendering overlay messages (e.g., action bar text).
     */
    ResourceLocation OVERLAY_MESSAGE = ResourceLocationHelper.withDefaultNamespace("overlay_message");
    /**
     * The layer for rendering titles and subtitles.
     */
    ResourceLocation TITLE = ResourceLocationHelper.withDefaultNamespace("title");
    /**
     * The layer for rendering the chat interface.
     */
    ResourceLocation CHAT = ResourceLocationHelper.withDefaultNamespace("chat");
    /**
     * The layer for rendering the player list (tab menu).
     */
    ResourceLocation PLAYER_LIST = ResourceLocationHelper.withDefaultNamespace("player_list");
    /**
     * The layer for rendering subtitles.
     */
    ResourceLocation SUBTITLES = ResourceLocationHelper.withDefaultNamespace("subtitles");

    /**
     * Register a new gui layer rendered after all existing layers.
     *
     * @param resourceLocation the gui layer resource location
     * @param guiLayer         the gui layer
     */
    void registerGuiLayer(ResourceLocation resourceLocation, Layer guiLayer);

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
    void registerGuiLayer(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, Layer guiLayer);

    /**
     * Replace an existing vanilla gui layer. Replacing custom layers is not supported.
     *
     * @param resourceLocation the vanilla gui layer resource location
     * @param guiLayerFactory  the gui layer factory, receiving the existing layer
     */
    void replaceGuiLayer(ResourceLocation resourceLocation, UnaryOperator<Layer> guiLayerFactory);

    @FunctionalInterface
    interface Layer {

        /**
         * Renders the gui layer.
         *
         * @param guiGraphics  the gui graphics
         * @param deltaTracker the delta tracker
         */
        void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    }
}
