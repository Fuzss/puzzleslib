package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

/**
 * Register new {@link Layer Layers} to be drawn as part of the {@link net.minecraft.client.gui.Gui}.
 */
public interface GuiLayersContext {
    /**
     * The layer for rendering camera-related overlays like pumpkin blur.
     */
    ResourceLocation CAMERA_OVERLAYS = ResourceLocation.withDefaultNamespace("camera_overlays");
    /**
     * The layer for rendering the crosshair.
     */
    ResourceLocation CROSSHAIR = ResourceLocation.withDefaultNamespace("crosshair");
    /**
     * The layer for rendering the hotbar.
     */
    ResourceLocation HOTBAR = ResourceLocation.withDefaultNamespace("hotbar");
    /**
     * The layer for rendering the info bars, like the experience bar, the locator bar, or the jump meter (e.g. for
     * horses).
     */
    ResourceLocation INFO_BAR = ResourceLocation.withDefaultNamespace("info_bar");
    /**
     * The layer for rendering the player's health hearts.
     */
    ResourceLocation PLAYER_HEALTH = ResourceLocation.withDefaultNamespace("player_health");
    /**
     * The layer for rendering the player's armor level.
     */
    ResourceLocation ARMOR_LEVEL = ResourceLocation.withDefaultNamespace("armor_level");
    /**
     * The layer for rendering the player's food level.
     */
    ResourceLocation FOOD_LEVEL = ResourceLocation.withDefaultNamespace("food_level");
    /**
     * The layer for rendering the health of the player's vehicle.
     */
    ResourceLocation VEHICLE_HEALTH = ResourceLocation.withDefaultNamespace("vehicle_health");
    /**
     * The layer for rendering the player's air level when submerged.
     */
    ResourceLocation AIR_LEVEL = ResourceLocation.withDefaultNamespace("air_level");
    /**
     * The layer for rendering the name of the selected item.
     */
    ResourceLocation HELD_ITEM_TOOLTIP = ResourceLocation.withDefaultNamespace("held_item_tooltip");
    /**
     * The layer for rendering the player's experience level number.
     */
    ResourceLocation EXPERIENCE_LEVEL = ResourceLocation.withDefaultNamespace("experience_level");
    /**
     * The layer for rendering the selected spectator menu action.
     */
    ResourceLocation SPECTATOR_TOOLTIP = ResourceLocation.withDefaultNamespace("spectator_tooltip");
    /**
     * The layer for rendering status effect icons.
     */
    ResourceLocation STATUS_EFFECTS = ResourceLocation.withDefaultNamespace("status_effects");
    /**
     * The layer for rendering boss bars.
     */
    ResourceLocation BOSS_BAR = ResourceLocation.withDefaultNamespace("boss_bar");
    /**
     * The layer for rendering the sleep overlay.
     */
    ResourceLocation SLEEP_OVERLAY = ResourceLocation.withDefaultNamespace("sleep_overlay");
    /**
     * The layer for rendering the demo mode timer.
     */
    ResourceLocation DEMO_TIMER = ResourceLocation.withDefaultNamespace("demo_timer");
    /**
     * The layer for rendering the scoreboard.
     */
    ResourceLocation SCOREBOARD = ResourceLocation.withDefaultNamespace("scoreboard");
    /**
     * The layer for rendering overlay messages (e.g., action bar text).
     */
    ResourceLocation OVERLAY_MESSAGE = ResourceLocation.withDefaultNamespace("overlay_message");
    /**
     * The layer for rendering titles and subtitles.
     */
    ResourceLocation TITLE = ResourceLocation.withDefaultNamespace("title");
    /**
     * The layer for rendering the chat interface.
     */
    ResourceLocation CHAT = ResourceLocation.withDefaultNamespace("chat");
    /**
     * The layer for rendering the player list (tab menu).
     */
    ResourceLocation PLAYER_LIST = ResourceLocation.withDefaultNamespace("player_list");
    /**
     * The layer for rendering subtitles.
     */
    ResourceLocation SUBTITLES = ResourceLocation.withDefaultNamespace("subtitles");

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

    /**
     * Register an additional height provider for a status bar layer rendered on the left side above the hotbar.
     * <p>
     * This is required for proper vertical positioning of the layer together with all other registered status bars.
     * <p>
     * To retrieve the render height for a status bar during rendering of the layer use
     * {@link fuzs.puzzleslib.api.client.gui.v2.ScreenHelper#getLeftStatusBarHeight(ResourceLocation)}.
     *
     * @param resourceLocation the gui layer resource location
     * @param heightProvider   the status bar height provider
     */
    void addLeftStatusBarHeightProvider(ResourceLocation resourceLocation, ToIntFunction<Player> heightProvider);

    /**
     * Register an additional height provider for a status bar layer rendered on the right side above the hotbar.
     * <p>
     * This is required for proper vertical positioning of the layer together with all other registered status bars.
     * <p>
     * To retrieve the render height for a status bar during rendering of the layer use
     * {@link fuzs.puzzleslib.api.client.gui.v2.ScreenHelper#getRightStatusBarHeight(ResourceLocation)}.
     *
     * @param resourceLocation the gui layer resource location
     * @param heightProvider   the status bar height provider
     */
    void addRightStatusBarHeightProvider(ResourceLocation resourceLocation, ToIntFunction<Player> heightProvider);

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
