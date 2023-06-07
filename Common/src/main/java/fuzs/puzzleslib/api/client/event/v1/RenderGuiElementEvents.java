package fuzs.puzzleslib.api.client.event.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Events for managing all the rendering done in {@link net.minecraft.client.gui.Gui}.
 * <p>This is modelled after Forge's system for handling individual components in the gui before Minecraft 1.17,
 * to avoid having to replace the whole gui renderer as Forge is doing in more recent versions.
 * <p>For convenience all ids are the same as the ones used by Forge's current gui system to ease implementation of these callbacks on Forge.
 */
public final class RenderGuiElementEvents {
    /**
     * The dark fading circle that shows around the whole gui.
     */
    public static final GuiOverlay VIGNETTE = new GuiOverlay("vignette");
    /**
     * The overlay that shows when zooming in using a spyglass.
     */
    public static final GuiOverlay SPYGLASS = new GuiOverlay("spyglass");
    /**
     * The pumpkin overlay that shows while wearing a pumpkin.
     */
    public static final GuiOverlay HELMET = new GuiOverlay("helmet");
    /**
     * The frost effect shown around the screen border when freezing, aka when standing in powdered snow.
     */
    public static final GuiOverlay FROSTBITE = new GuiOverlay("frostbite");
    /**
     * The purple portal overlay distorting the screen when standing inside a nether portal.
     */
    public static final GuiOverlay PORTAL = new GuiOverlay("portal");
    /**
     * The hotbar shown on the bottom screen.
     */
    public static final GuiOverlay HOTBAR = new GuiOverlay("hotbar", minecraft -> !minecraft.options.hideGui && minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR);
    /**
     * The cross-hair shown in the center of the screen, includes the cross-hair attack indicator.
     */
    public static final GuiOverlay CROSSHAIR = new GuiOverlay("crosshair", minecraft -> !minecraft.options.hideGui);
    /**
     * The colorful health bar that shows while near a boss mob (ender dragon and wither in vanilla).
     */
    public static final GuiOverlay BOSS_EVENT_PROGRESS = new GuiOverlay("boss_event_progress", minecraft -> !minecraft.options.hideGui);
    /**
     * The hearts representing the player's current health shown to the left above the hotbar.
     */
    public static final GuiOverlay PLAYER_HEALTH = new GuiOverlay("player_health", minecraft -> !minecraft.options.hideGui);
    /**
     * The armor icons representing the player's current protection level shown to the left above the hotbar.
     */
    public static final GuiOverlay ARMOR_LEVEL = new GuiOverlay("armor_level", minecraft -> !minecraft.options.hideGui);
    /**
     * The little meat shanks representing the player's current food level shown to the right above the hotbar.
     */
    public static final GuiOverlay FOOD_LEVEL = new GuiOverlay("food_level", minecraft -> !minecraft.options.hideGui);
    /**
     * The hearts representing the player's current mount's health shown to the right above the hotbar.
     */
    public static final GuiOverlay MOUNT_HEALTH = new GuiOverlay("mount_health", minecraft -> !minecraft.options.hideGui);
    /**
     * The air bubbles representing the player's left air supply while underwater shown to the right above the hotbar.
     */
    public static final GuiOverlay AIR_LEVEL = new GuiOverlay("air_level", minecraft -> !minecraft.options.hideGui);
    /**
     * The jump bar shown when riding a mount that can jump such as horses, replaces the experience bar while active.
     */
    public static final GuiOverlay JUMP_BAR = new GuiOverlay("jump_bar", minecraft -> !minecraft.options.hideGui);
    /**
     * A bar representing the player's current experience level progress shown above the hotbar.
     */
    public static final GuiOverlay EXPERIENCE_BAR = new GuiOverlay("experience_bar", minecraft -> !minecraft.options.hideGui);
    /**
     * The name of the currently selected hotbar item shown right above the hotbar for a few seconds right after switching to that item.
     */
    public static final GuiOverlay ITEM_NAME = new GuiOverlay("item_name", minecraft -> !minecraft.options.hideGui && minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR);
    /**
     * The screen fade effect that increasingly intensifies the longer the player lies in a bed.
     */
    public static final GuiOverlay SLEEP_FADE = new GuiOverlay("sleep_fade");
    /**
     * The debug screen that is toggled using <code>F3</code>.
     */
    public static final GuiOverlay DEBUG_TEXT = new GuiOverlay("debug_text");
    /**
     * The fps graph which is part of the debug screen, but must be separately toggled by opening the debug screen while also holding <code>Alt</code>.
     */
    public static final GuiOverlay FPS_GRAPH = new GuiOverlay("fps_graph");
    /**
     * The widgets showing the player's active {@link net.minecraft.world.effect.MobEffect}s in the top right corner of the screen.
     */
    public static final GuiOverlay POTION_ICONS = new GuiOverlay("potion_icons");
    /**
     * The title of the current record that is playing in a nearby jukebox shown above the hotbar.
     */
    public static final GuiOverlay RECORD_OVERLAY = new GuiOverlay("record_overlay", minecraft -> !minecraft.options.hideGui);
    /**
     * Subtitles for in-game sound events shown in the bottom right of the screen.
     */
    public static final GuiOverlay SUBTITLES = new GuiOverlay("subtitles", minecraft -> !minecraft.options.hideGui);
    /**
     * A huge text shown in the center of the screen, triggered by the <code>/title</code> command.
     */
    public static final GuiOverlay TITLE_TEXT = new GuiOverlay("title_text", minecraft -> !minecraft.options.hideGui);
    /**
     * The scoreboard display shown to the right of the screen.
     */
    public static final GuiOverlay SCOREBOARD = new GuiOverlay("scoreboard");
    /**
     * The display for incoming chat messages.
     */
    public static final GuiOverlay CHAT_PANEL = new GuiOverlay("chat_panel");
    /**
     * The list of online players on a server that shows centered at the top of the screen while the tab key is held.
     */
    public static final GuiOverlay PLAYER_LIST = new GuiOverlay("player_list");

    private RenderGuiElementEvents() {

    }

    public static EventInvoker<Before> before(GuiOverlay id) {
        Objects.requireNonNull(id, "id is null");
        return EventInvoker.lookup(Before.class, id);
    }

    public static EventInvoker<After> after(GuiOverlay id) {
        Objects.requireNonNull(id, "id is null");
        return EventInvoker.lookup(After.class, id);
    }

    /**
     * A simple id storage for a gui overlay, additionally can support a predicate for applying more precisely.
     *
     * @param id the identifier for this overlay type
     * @param filter an optional filter to better help match the implementation between different mod loaders
     */
    public record GuiOverlay(ResourceLocation id, Predicate<Minecraft> filter) {

        public GuiOverlay(String id) {
            this(new ResourceLocation(id));
        }

        public GuiOverlay(ResourceLocation id) {
            this(id, minecraft -> true);
        }

        public GuiOverlay(String id, Predicate<Minecraft> filter) {
            this(new ResourceLocation(id), filter);
        }

        public GuiOverlay {
            Objects.requireNonNull(id, "id is null");
            Objects.requireNonNull(filter, "filter is null");
        }
    }

    @FunctionalInterface
    public interface Before {

        /**
         * Called before a gui element is rendered, allows for cancelling rendering.
         *
         * @param minecraft    minecraft singleton instance
         * @param poseStack    the pose stack
         * @param tickDelta    partial tick time
         * @param screenWidth  width of the window's screen
         * @param screenHeight height of the window's screen
         * @return {@link EventResult#INTERRUPT} to prevent the element from rendering,
         * {@link EventResult#PASS} to allow the element to render normally
         */
        EventResult onBeforeRenderGuiElement(Minecraft minecraft, PoseStack poseStack, float tickDelta, int screenWidth, int screenHeight);
    }

    @FunctionalInterface
    public interface After {

        /**
         * Called after a gui element is rendered.
         *
         * @param minecraft    minecraft singleton instance
         * @param poseStack    the pose stack
         * @param tickDelta    partial tick time
         * @param screenWidth  width of the window's screen
         * @param screenHeight height of the window's screen
         */
        void onAfterRenderGuiElement(Minecraft minecraft, PoseStack poseStack, float tickDelta, int screenWidth, int screenHeight);
    }
}
