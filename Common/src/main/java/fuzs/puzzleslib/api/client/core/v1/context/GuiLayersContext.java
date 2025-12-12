package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
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
    Identifier CAMERA_OVERLAYS = Identifier.withDefaultNamespace("camera_overlays");
    /**
     * The layer for rendering the crosshair.
     */
    Identifier CROSSHAIR = Identifier.withDefaultNamespace("crosshair");
    /**
     * The layer for rendering the hotbar.
     */
    Identifier HOTBAR = Identifier.withDefaultNamespace("hotbar");
    /**
     * The layer for rendering the info bars, like the experience bar, the locator bar, or the jump meter (e.g. for
     * horses).
     */
    Identifier INFO_BAR = Identifier.withDefaultNamespace("info_bar");
    /**
     * The layer for rendering the player's health hearts.
     */
    Identifier PLAYER_HEALTH = Identifier.withDefaultNamespace("player_health");
    /**
     * The layer for rendering the player's armor level.
     */
    Identifier ARMOR_LEVEL = Identifier.withDefaultNamespace("armor_level");
    /**
     * The layer for rendering the player's food level.
     */
    Identifier FOOD_LEVEL = Identifier.withDefaultNamespace("food_level");
    /**
     * The layer for rendering the health of the player's vehicle.
     */
    Identifier VEHICLE_HEALTH = Identifier.withDefaultNamespace("vehicle_health");
    /**
     * The layer for rendering the player's air level when submerged.
     */
    Identifier AIR_LEVEL = Identifier.withDefaultNamespace("air_level");
    /**
     * The layer for rendering the name of the selected item.
     */
    Identifier HELD_ITEM_TOOLTIP = Identifier.withDefaultNamespace("held_item_tooltip");
    /**
     * The layer for rendering the player's experience level number.
     */
    Identifier EXPERIENCE_LEVEL = Identifier.withDefaultNamespace("experience_level");
    /**
     * The layer for rendering the selected spectator menu action.
     */
    Identifier SPECTATOR_TOOLTIP = Identifier.withDefaultNamespace("spectator_tooltip");
    /**
     * The layer for rendering status effect icons.
     */
    Identifier STATUS_EFFECTS = Identifier.withDefaultNamespace("status_effects");
    /**
     * The layer for rendering boss bars.
     */
    Identifier BOSS_BAR = Identifier.withDefaultNamespace("boss_bar");
    /**
     * The layer for rendering the sleep overlay.
     */
    Identifier SLEEP_OVERLAY = Identifier.withDefaultNamespace("sleep_overlay");
    /**
     * The layer for rendering the demo mode timer.
     */
    Identifier DEMO_TIMER = Identifier.withDefaultNamespace("demo_timer");
    /**
     * The layer for rendering the scoreboard.
     */
    Identifier SCOREBOARD = Identifier.withDefaultNamespace("scoreboard");
    /**
     * The layer for rendering overlay messages (e.g., action bar text).
     */
    Identifier OVERLAY_MESSAGE = Identifier.withDefaultNamespace("overlay_message");
    /**
     * The layer for rendering titles and subtitles.
     */
    Identifier TITLE = Identifier.withDefaultNamespace("title");
    /**
     * The layer for rendering the chat interface.
     */
    Identifier CHAT = Identifier.withDefaultNamespace("chat");
    /**
     * The layer for rendering the player list (tab menu).
     */
    Identifier PLAYER_LIST = Identifier.withDefaultNamespace("player_list");
    /**
     * The layer for rendering subtitles.
     */
    Identifier SUBTITLES = Identifier.withDefaultNamespace("subtitles");

    /**
     * Register a new gui layer rendered after all existing layers.
     *
     * @param identifier the gui layer identifier
     * @param guiLayer         the gui layer
     */
    void registerGuiLayer(Identifier identifier, Layer guiLayer);

    /**
     * Register a new gui layer rendered before or after an existing vanilla gui layer.
     * <p>
     * The ordering depends on the order in which both identifier arguments are passed.
     *
     * @param identifier the gui layer identifier, either for the new layer or for the existing vanilla
     *                         layer
     * @param otherIdentifier  the other gui layer identifier, either for the new layer or for the existing
     *                         vanilla layer
     * @param guiLayer         the gui layer
     */
    void registerGuiLayer(Identifier identifier, Identifier otherIdentifier, Layer guiLayer);

    /**
     * Replace an existing vanilla gui layer. Replacing custom layers is not supported.
     *
     * @param identifier the vanilla gui layer identifier
     * @param guiLayerFactory  the gui layer factory, receiving the existing layer
     */
    void replaceGuiLayer(Identifier identifier, UnaryOperator<Layer> guiLayerFactory);

    /**
     * Register an additional height provider for a status bar layer rendered on the left side above the hotbar.
     * <p>
     * This is required for proper vertical positioning of the layer together with all other registered status bars.
     * <p>
     * To retrieve the render height for a status bar during rendering of the layer use
     * {@link fuzs.puzzleslib.api.client.gui.v2.ScreenHelper#getLeftStatusBarHeight(Identifier)}.
     *
     * @param identifier the gui layer identifier
     * @param heightProvider   the status bar height provider
     */
    void addLeftStatusBarHeightProvider(Identifier identifier, ToIntFunction<Player> heightProvider);

    /**
     * Register an additional height provider for a status bar layer rendered on the right side above the hotbar.
     * <p>
     * This is required for proper vertical positioning of the layer together with all other registered status bars.
     * <p>
     * To retrieve the render height for a status bar during rendering of the layer use
     * {@link fuzs.puzzleslib.api.client.gui.v2.ScreenHelper#getRightStatusBarHeight(Identifier)}.
     *
     * @param identifier the gui layer identifier
     * @param heightProvider   the status bar height provider
     */
    void addRightStatusBarHeightProvider(Identifier identifier, ToIntFunction<Player> heightProvider);

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
