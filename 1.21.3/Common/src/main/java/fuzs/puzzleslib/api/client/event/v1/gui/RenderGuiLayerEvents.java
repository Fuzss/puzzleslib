package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Events for managing all the rendering in {@link net.minecraft.client.gui.Gui#render(GuiGraphics, DeltaTracker)}.
 * <p>
 * Respects {@link net.minecraft.client.Options#hideGui}, for rendering custom gui layers regardless of that setting use
 * {@link RenderGuiEvents.After}.
 * <p>
 * This is modelled after NeoForge's system for handling individual gui layers, for convenience all ids are the same as
 * the ones used there.
 * <p>
 * Note that the implementation on Fabric runs before any actual vanilla gui layers are drawn with similar z-offsets.
 * This is unfortunately necessary to support a proper rendering order that can correctly update
 * {@link fuzs.puzzleslib.api.client.core.v1.ClientAbstractions#getGuiLeftHeight(Gui)} &amp;
 * {@link fuzs.puzzleslib.api.client.core.v1.ClientAbstractions#getGuiRightHeight(Gui)} while running. Also some gui
 * layer events currently cannot be cancelled on Fabric as they are not yet fully implemented.
 * <p>
 * TODO replace {@link Minecraft} argument with {@link net.minecraft.client.gui.Gui}
 */
public final class RenderGuiLayerEvents {
    private static final List<ResourceLocation> VANILLA_GUI_LAYERS = new ArrayList<>();
    public static final List<ResourceLocation> VANILLA_GUI_LAYERS_VIEW = Collections.unmodifiableList(
            VANILLA_GUI_LAYERS);
    public static final ResourceLocation CAMERA_OVERLAYS = register("camera_overlays");
    public static final ResourceLocation CROSSHAIR = register("crosshair");
    public static final ResourceLocation HOTBAR = register("hotbar");
    public static final ResourceLocation JUMP_METER = register("jump_meter");
    public static final ResourceLocation EXPERIENCE_BAR = register("experience_bar");
    public static final ResourceLocation PLAYER_HEALTH = register("player_health");
    public static final ResourceLocation ARMOR_LEVEL = register("armor_level");
    public static final ResourceLocation FOOD_LEVEL = register("food_level");
    public static final ResourceLocation VEHICLE_HEALTH = register("vehicle_health");
    public static final ResourceLocation AIR_LEVEL = register("air_level");
    public static final ResourceLocation SELECTED_ITEM_NAME = register("selected_item_name");
    public static final ResourceLocation SPECTATOR_TOOLTIP = register("spectator_tooltip");
    public static final ResourceLocation EXPERIENCE_LEVEL = register("experience_level");
    public static final ResourceLocation EFFECTS = register("effects");
    public static final ResourceLocation BOSS_OVERLAY = register("boss_overlay");
    public static final ResourceLocation SLEEP_OVERLAY = register("sleep_overlay");
    public static final ResourceLocation DEMO_OVERLAY = register("demo_overlay");
    public static final ResourceLocation DEBUG_OVERLAY = register("debug_overlay");
    public static final ResourceLocation SCOREBOARD_SIDEBAR = register("scoreboard_sidebar");
    public static final ResourceLocation OVERLAY_MESSAGE = register("overlay_message");
    public static final ResourceLocation TITLE = register("title");
    public static final ResourceLocation CHAT = register("chat");
    public static final ResourceLocation TAB_LIST = register("tab_list");
    public static final ResourceLocation SUBTITLE_OVERLAY = register("subtitle_overlay");
    public static final ResourceLocation SAVING_INDICATOR = register("saving_indicator");

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

    private static ResourceLocation register(String path) {
        return register(ResourceLocationHelper.withDefaultNamespace(path));
    }

    private static ResourceLocation register(ResourceLocation resourceLocation) {
        VANILLA_GUI_LAYERS.add(resourceLocation);
        return resourceLocation;
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
