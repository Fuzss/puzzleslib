package fuzs.puzzleslib.api.client.event.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

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
    public static final ResourceLocation VIGNETTE = new ResourceLocation("vignette");
    /**
     * The overlay that shows when zooming in using a spyglass.
     */
    public static final ResourceLocation SPYGLASS = new ResourceLocation("spyglass");
    /**
     * The pumpkin overlay that shows while wearing a pumpkin.
     */
    public static final ResourceLocation HELMET = new ResourceLocation("helmet");
    /**
     * The frost effect shown around the screen border when freezing, aka when standing in powdered snow.
     */
    public static final ResourceLocation FROSTBITE = new ResourceLocation("frostbite");
    /**
     * The purple portal overlay distorting the screen when standing inside a nether portal.
     */
    public static final ResourceLocation PORTAL = new ResourceLocation("portal");
    /**
     * The hotbar shown on the bottom screen.
     */
    public static final ResourceLocation HOTBAR = new ResourceLocation("hotbar");
    /**
     * The cross-hair shown in the center of the screen, includes the cross-hair attack indicator.
     */
    public static final ResourceLocation CROSSHAIR = new ResourceLocation("crosshair");
    /**
     * The colorful health bar that shows while near a boss mob (ender dragon and wither in vanilla).
     */
    public static final ResourceLocation BOSS_EVENT_PROGRESS = new ResourceLocation("boss_event_progress");
    /**
     * The hearts representing the player's current health shown to the left above the hotbar.
     */
    public static final ResourceLocation PLAYER_HEALTH = new ResourceLocation("player_health");
    /**
     * The armor icons representing the player's current protection level shown to the left above the hotbar.
     */
    public static final ResourceLocation ARMOR_LEVEL = new ResourceLocation("armor_level");
    /**
     * The little meat shanks representing the player's current food level shown to the right above the hotbar.
     */
    public static final ResourceLocation FOOD_LEVEL = new ResourceLocation("food_level");
    /**
     * The hearts representing the player's current mount's health shown to the right above the hotbar.
     */
    public static final ResourceLocation MOUNT_HEALTH = new ResourceLocation("mount_health");
    /**
     * The air bubbles representing the player's left air supply while underwater shown to the right above the hotbar.
     */
    public static final ResourceLocation AIR_LEVEL = new ResourceLocation("air_level");
    /**
     * The jump bar shown when riding a mount that can jump such as horses, replaces the experience bar while active.
     */
    public static final ResourceLocation JUMP_BAR = new ResourceLocation("jump_bar");
    /**
     * A bar representing the player's current experience level progress shown above the hotbar.
     */
    public static final ResourceLocation EXPERIENCE_BAR = new ResourceLocation("experience_bar");
    /**
     * The name of the currently selected hotbar item shown right above the hotbar for a few seconds right after switching to that item.
     */
    public static final ResourceLocation ITEM_NAME = new ResourceLocation("item_name");
    /**
     * The screen fade effect that increasingly intensifies the longer the player lies in a bed.
     */
    public static final ResourceLocation SLEEP_FADE = new ResourceLocation("sleep_fade");
    /**
     * The debug screen that is toggled using <code>F3</code>.
     */
    public static final ResourceLocation DEBUG_TEXT = new ResourceLocation("debug_text");
    /**
     * The fps graph which is part of the debug screen, but must be separately toggled by opening the debug screen while also holding <code>Alt</code>.
     */
    public static final ResourceLocation FPS_GRAPH = new ResourceLocation("fps_graph");
    /**
     * The widgets showing the player's active {@link net.minecraft.world.effect.MobEffect}s in the top right corner of the screen.
     */
    public static final ResourceLocation POTION_ICONS = new ResourceLocation("potion_icons");
    /**
     * The title of the current record that is playing in a nearby jukebox shown above the hotbar.
     */
    public static final ResourceLocation RECORD_OVERLAY = new ResourceLocation("record_overlay");
    /**
     * Subtitles for in-game sound events shown in the bottom right of the screen.
     */
    public static final ResourceLocation SUBTITLES = new ResourceLocation("subtitles");
    /**
     * A huge text shown in the center of the screen, triggered by the <code>/title</code> command.
     */
    public static final ResourceLocation TITLE_TEXT = new ResourceLocation("title_text");
    /**
     * The scoreboard display shown to the right of the screen.
     */
    public static final ResourceLocation SCOREBOARD = new ResourceLocation("scoreboard");
    /**
     * The display for incoming chat messages.
     */
    public static final ResourceLocation CHAT_PANEL = new ResourceLocation("chat_panel");
    /**
     * The list of online players on a server that shows centered at the top of the screen while the tab key is held.
     */
    public static final ResourceLocation PLAYER_LIST = new ResourceLocation("player_list");

    private RenderGuiElementEvents() {

    }

    public static EventInvoker<Before> before(ResourceLocation id) {
        Objects.requireNonNull(id, "id is null");
        return EventInvoker.lookup(Before.class, id);
    }

    public static EventInvoker<Before> after(ResourceLocation id) {
        Objects.requireNonNull(id, "id is null");
        return EventInvoker.lookup(Before.class, id);
    }

    @FunctionalInterface
    public interface Before {

        /**
         * Called before a gui element is rendered, allows for cancelling rendering.
         *
         * @param poseStack    the pose stack
         * @param screenWidth  width of the window's screen
         * @param screenHeight height of the window's screen
         * @return {@link EventResult#INTERRUPT} to prevent the element from rendering,
         * {@link EventResult#PASS} to allow the element to render normally
         */
        EventResult onBeforeRenderGuiElement(PoseStack poseStack, int screenWidth, int screenHeight);
    }

    @FunctionalInterface
    public interface After {

        /**
         * Called after a gui element is rendered.
         *
         * @param poseStack    the pose stack
         * @param screenWidth  width of the window's screen
         * @param screenHeight height of the window's screen
         */
        void onAfterRenderGuiElement(PoseStack poseStack, int screenWidth, int screenHeight);
    }
}
