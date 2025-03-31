package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

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
 * {@link fuzs.puzzleslib.api.client.gui.v2.GuiHeightHelper#getRightHeight(Gui)} &amp;
 * {@link fuzs.puzzleslib.api.client.gui.v2.GuiHeightHelper#getLeftHeight(Gui)} while running. Also, some gui layer
 * events currently cannot be cancelled on Fabric as they are not yet fully implemented.
 */
public final class RenderGuiLayerEvents {
    private static final List<ResourceLocation> VANILLA_GUI_LAYERS = new ArrayList<>();
    public static final List<ResourceLocation> VANILLA_GUI_LAYERS_VIEW = Collections.unmodifiableList(VANILLA_GUI_LAYERS);
    public static final ResourceLocation CAMERA_OVERLAYS = registerGuiLayer("camera_overlays");
    public static final ResourceLocation CROSSHAIR = registerGuiLayer("crosshair");
    public static final ResourceLocation HOTBAR = registerGuiLayer("hotbar");
    public static final ResourceLocation JUMP_METER = registerGuiLayer("jump_meter");
    public static final ResourceLocation EXPERIENCE_BAR = registerGuiLayer("experience_bar");
    public static final ResourceLocation PLAYER_HEALTH = registerGuiLayer("player_health");
    public static final ResourceLocation ARMOR_LEVEL = registerGuiLayer("armor_level");
    public static final ResourceLocation FOOD_LEVEL = registerGuiLayer("food_level");
    public static final ResourceLocation VEHICLE_HEALTH = registerGuiLayer("vehicle_health");
    public static final ResourceLocation AIR_LEVEL = registerGuiLayer("air_level");
    public static final ResourceLocation SELECTED_ITEM_NAME = registerGuiLayer("selected_item_name");
    public static final ResourceLocation SPECTATOR_TOOLTIP = registerGuiLayer("spectator_tooltip");
    public static final ResourceLocation EXPERIENCE_LEVEL = registerGuiLayer("experience_level");
    public static final ResourceLocation STATUS_EFFECTS = registerGuiLayer("status_effects");
    public static final ResourceLocation BOSS_BAR = registerGuiLayer("boss_bar");
    public static final ResourceLocation SLEEP_OVERLAY = registerGuiLayer("sleep_overlay");
    public static final ResourceLocation DEMO_TIMER = registerGuiLayer("demo_timer");
    public static final ResourceLocation DEBUG_OVERLAY = registerGuiLayer("debug_overlay");
    public static final ResourceLocation SCOREBOARD = registerGuiLayer("scoreboard");
    public static final ResourceLocation OVERLAY_MESSAGE = registerGuiLayer("overlay_message");
    public static final ResourceLocation TITLE = registerGuiLayer("title");
    public static final ResourceLocation CHAT = registerGuiLayer("chat");
    public static final ResourceLocation PLAYER_LIST = registerGuiLayer("player_list");
    public static final ResourceLocation SUBTITLES = registerGuiLayer("subtitles");
    public static final ResourceLocation SAVING_INDICATOR = registerGuiLayer("saving_indicator");

    private RenderGuiLayerEvents() {
        // NO-OP
    }

    public static EventInvoker<Before> before(ResourceLocation resourceLocation) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        return EventInvoker.lookup(Before.class, getModLoaderGuiLayer(resourceLocation));
    }

    public static EventInvoker<After> after(ResourceLocation resourceLocation) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        return EventInvoker.lookup(After.class, getModLoaderGuiLayer(resourceLocation));
    }

    @ApiStatus.Internal
    public static ResourceLocation getModLoaderGuiLayer(ResourceLocation resourceLocation) {
        ResourceLocation modLoaderGuiLayer = ClientProxyImpl.get().getModLoaderGuiLayer(resourceLocation);
        Objects.requireNonNull(modLoaderGuiLayer, "gui layer for " + resourceLocation + " is null");
        return modLoaderGuiLayer;
    }

    private static ResourceLocation registerGuiLayer(String path) {
        return registerGuiLayer(ResourceLocationHelper.withDefaultNamespace(path));
    }

    public static ResourceLocation registerGuiLayer(ResourceLocation resourceLocation) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        return register(resourceLocation, null);
    }

    public static ResourceLocation registerGuiLayer(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(otherResourceLocation, "other resource location is null");
        return register(resourceLocation, otherResourceLocation);
    }

    private static ResourceLocation register(ResourceLocation resourceLocation, @Nullable ResourceLocation otherResourceLocation) {
        if (resourceLocation != null && otherResourceLocation == null) {
            VANILLA_GUI_LAYERS.add(resourceLocation);
            return resourceLocation;
        } else {
            int resourceLocationIndex = VANILLA_GUI_LAYERS.indexOf(resourceLocation);
            int otherResourceLocationIndex = VANILLA_GUI_LAYERS.indexOf(otherResourceLocation);
            if (resourceLocationIndex != -1 && otherResourceLocationIndex == -1) {
                // resourceLocation exists, otherResourceLocation should be added afterward
                VANILLA_GUI_LAYERS.add(resourceLocationIndex + 1, otherResourceLocation);
                return otherResourceLocation;
            } else if (resourceLocationIndex == -1 && otherResourceLocationIndex != -1) {
                // otherResourceLocation exists, resourceLocation should be added before
                VANILLA_GUI_LAYERS.add(otherResourceLocationIndex, resourceLocation);
                return resourceLocation;
            } else {
                throw new RuntimeException(
                        "Invalid resource location indices: " + resourceLocation + "=" + resourceLocationIndex + ", " +
                                otherResourceLocation + "=" + otherResourceLocationIndex);
            }
        }
    }

    @FunctionalInterface
    public interface Before {

        /**
         * Called before a gui element is rendered, allows for cancelling rendering.
         *
         * @param gui          the gui instance
         * @param guiGraphics  the gui graphics component
         * @param deltaTracker partial tick time
         * @return {@link EventResult#INTERRUPT} to prevent the element from rendering, {@link EventResult#PASS} to
         *         allow the element to render normally
         */
        EventResult onBeforeRenderGuiLayer(Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    }

    @FunctionalInterface
    public interface After {

        /**
         * Called after a gui element is rendered.
         *
         * @param gui          the gui instance
         * @param guiGraphics  the gui graphics component
         * @param deltaTracker partial tick time
         */
        void onAfterRenderGuiLayer(Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    }
}
