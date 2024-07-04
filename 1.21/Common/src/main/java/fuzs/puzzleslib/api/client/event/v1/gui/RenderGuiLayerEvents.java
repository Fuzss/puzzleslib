package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * Events for managing all the rendering done in {@link net.minecraft.client.gui.Gui}.
 * <p>
 * This is modelled after Forge's system for handling individual components in the gui before Minecraft 1.17, to avoid
 * having to replace the whole gui renderer as Forge is doing in more recent versions.
 * <p>
 * For convenience all ids are the same as the ones used by Forge's current gui system to ease implementation of these
 * callbacks on Forge.
 */
public final class RenderGuiLayerEvents {
    public static final ResourceLocation CAMERA_OVERLAYS = ResourceLocationHelper.withDefaultNamespace("camera_overlays");
    public static final ResourceLocation CROSSHAIR = ResourceLocationHelper.withDefaultNamespace("crosshair");
    public static final ResourceLocation HOTBAR = ResourceLocationHelper.withDefaultNamespace("hotbar");
    public static final ResourceLocation JUMP_METER = ResourceLocationHelper.withDefaultNamespace("jump_meter");
    public static final ResourceLocation EXPERIENCE_BAR = ResourceLocationHelper.withDefaultNamespace("experience_bar");
    public static final ResourceLocation PLAYER_HEALTH = ResourceLocationHelper.withDefaultNamespace("player_health");
    public static final ResourceLocation ARMOR_LEVEL = ResourceLocationHelper.withDefaultNamespace("armor_level");
    public static final ResourceLocation FOOD_LEVEL = ResourceLocationHelper.withDefaultNamespace("food_level");
    public static final ResourceLocation VEHICLE_HEALTH = ResourceLocationHelper.withDefaultNamespace("vehicle_health");
    public static final ResourceLocation AIR_LEVEL = ResourceLocationHelper.withDefaultNamespace("air_level");
    public static final ResourceLocation SELECTED_ITEM_NAME = ResourceLocationHelper.withDefaultNamespace(
            "selected_item_name");
    public static final ResourceLocation SPECTATOR_TOOLTIP = ResourceLocationHelper.withDefaultNamespace(
            "spectator_tooltip");
    public static final ResourceLocation EXPERIENCE_LEVEL = ResourceLocationHelper.withDefaultNamespace(
            "experience_level");
    public static final ResourceLocation EFFECTS = ResourceLocationHelper.withDefaultNamespace("effects");
    public static final ResourceLocation BOSS_OVERLAY = ResourceLocationHelper.withDefaultNamespace("boss_overlay");
    public static final ResourceLocation SLEEP_OVERLAY = ResourceLocationHelper.withDefaultNamespace("sleep_overlay");
    public static final ResourceLocation DEMO_OVERLAY = ResourceLocationHelper.withDefaultNamespace("demo_overlay");
    public static final ResourceLocation DEBUG_OVERLAY = ResourceLocationHelper.withDefaultNamespace("debug_overlay");
    public static final ResourceLocation SCOREBOARD_SIDEBAR = ResourceLocationHelper.withDefaultNamespace(
            "scoreboard_sidebar");
    public static final ResourceLocation OVERLAY_MESSAGE = ResourceLocationHelper.withDefaultNamespace("overlay_message");
    public static final ResourceLocation TITLE = ResourceLocationHelper.withDefaultNamespace("title");
    public static final ResourceLocation CHAT = ResourceLocationHelper.withDefaultNamespace("chat");
    public static final ResourceLocation TAB_LIST = ResourceLocationHelper.withDefaultNamespace("tab_list");
    public static final ResourceLocation SUBTITLE_OVERLAY = ResourceLocationHelper.withDefaultNamespace(
            "subtitle_overlay");
    public static final ResourceLocation SAVING_INDICATOR = ResourceLocationHelper.withDefaultNamespace(
            "saving_indicator");

    private RenderGuiLayerEvents() {
        // NO-OP
    }

    public static EventInvoker<Before> before(ResourceLocation resourceLocation) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        return EventInvoker.lookup(Before.class, resourceLocation);
    }

    public static EventInvoker<After> after(ResourceLocation resourceLocation) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        return EventInvoker.lookup(After.class, resourceLocation);
    }

    @FunctionalInterface
    public interface Before {

        /**
         * Called before a gui element is rendered, allows for cancelling rendering.
         *
         * @param minecraft    minecraft singleton instance
         * @param guiGraphics  the gui graphics component
         * @param deltaTracker partial tick time
         * @return {@link EventResult#INTERRUPT} to prevent the element from rendering, {@link EventResult#PASS} to
         *         allow the element to render normally
         */
        EventResult onBeforeRenderGuiLayer(Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    }

    @FunctionalInterface
    public interface After {

        /**
         * Called after a gui element is rendered.
         *
         * @param minecraft    minecraft singleton instance
         * @param guiGraphics  the gui graphics component
         * @param deltaTracker partial tick time
         */
        void onAfterRenderGuiLayer(Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    }
}
