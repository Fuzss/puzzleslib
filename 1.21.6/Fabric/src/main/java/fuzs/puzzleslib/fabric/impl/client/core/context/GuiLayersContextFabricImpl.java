package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.fabric.api.client.gui.v2.HudStatusBarHeightRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.Objects;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

public final class GuiLayersContextFabricImpl implements GuiLayersContext {
    private static final Map<ResourceLocation, ResourceLocation> VANILLA_GUI_LAYERS = ImmutableMap.<ResourceLocation, ResourceLocation>builder()
            .put(CAMERA_OVERLAYS, VanillaHudElements.MISC_OVERLAYS)
            .put(CROSSHAIR, VanillaHudElements.CROSSHAIR)
            .put(HOTBAR, VanillaHudElements.HOTBAR)
            .put(INFO_BAR, VanillaHudElements.INFO_BAR)
            .put(PLAYER_HEALTH, VanillaHudElements.HEALTH_BAR)
            .put(ARMOR_LEVEL, VanillaHudElements.ARMOR_BAR)
            .put(FOOD_LEVEL, VanillaHudElements.FOOD_BAR)
            .put(VEHICLE_HEALTH, VanillaHudElements.MOUNT_HEALTH)
            .put(AIR_LEVEL, VanillaHudElements.AIR_BAR)
            .put(HELD_ITEM_TOOLTIP, VanillaHudElements.HELD_ITEM_TOOLTIP)
            .put(EXPERIENCE_LEVEL, VanillaHudElements.EXPERIENCE_LEVEL)
            .put(SPECTATOR_TOOLTIP, VanillaHudElements.SPECTATOR_TOOLTIP)
            .put(STATUS_EFFECTS, VanillaHudElements.STATUS_EFFECTS)
            .put(BOSS_BAR, VanillaHudElements.BOSS_BAR)
            .put(SLEEP_OVERLAY, VanillaHudElements.SLEEP)
            .put(DEMO_TIMER, VanillaHudElements.DEMO_TIMER)
            .put(DEBUG_OVERLAY, VanillaHudElements.DEBUG)
            .put(SCOREBOARD, VanillaHudElements.SCOREBOARD)
            .put(OVERLAY_MESSAGE, VanillaHudElements.OVERLAY_MESSAGE)
            .put(TITLE, VanillaHudElements.TITLE_AND_SUBTITLE)
            .put(CHAT, VanillaHudElements.CHAT)
            .put(PLAYER_LIST, VanillaHudElements.PLAYER_LIST)
            .put(SUBTITLES, VanillaHudElements.SUBTITLES)
            .build();

    @Override
    public void registerGuiLayer(ResourceLocation resourceLocation, GuiLayersContext.Layer guiLayer) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(guiLayer, "gui layer is null");
        HudElementRegistry.addLast(resourceLocation, guiLayer::render);
    }

    @Override
    public void registerGuiLayer(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, GuiLayersContext.Layer guiLayer) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(otherResourceLocation, "other resource location is null");
        Objects.requireNonNull(guiLayer, "gui layer is null");
        // only check for vanilla layers, it simplifies the implementation and is all we need
        if (VANILLA_GUI_LAYERS.containsKey(resourceLocation)) {
            HudElementRegistry.attachElementAfter(VANILLA_GUI_LAYERS.get(resourceLocation),
                    otherResourceLocation,
                    guiLayer::render);
        } else if (VANILLA_GUI_LAYERS.containsKey(otherResourceLocation)) {
            HudElementRegistry.attachElementBefore(VANILLA_GUI_LAYERS.get(otherResourceLocation),
                    resourceLocation,
                    guiLayer::render);
        } else {
            throw new RuntimeException("Unknown gui layers: " + resourceLocation + ", " + otherResourceLocation);
        }
    }

    @Override
    public void replaceGuiLayer(ResourceLocation resourceLocation, UnaryOperator<GuiLayersContext.Layer> guiLayerFactory) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(guiLayerFactory, "gui layer factory is null");
        // only check for vanilla layers, it simplifies the implementation and is all we need
        if (VANILLA_GUI_LAYERS.containsKey(resourceLocation)) {
            HudElementRegistry.replaceElement(VANILLA_GUI_LAYERS.get(resourceLocation), (HudElement hudElement) -> {
                return guiLayerFactory.apply(hudElement::render)::render;
            });
        } else {
            throw new RuntimeException("Unknown gui layer: " + resourceLocation);
        }
    }

    @Override
    public void addLeftStatusBarHeightProvider(ResourceLocation resourceLocation, ToIntFunction<Player> heightProvider) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(heightProvider, "height provider is null");
        HudStatusBarHeightRegistry.addLeft(resourceLocation, heightProvider);
    }

    @Override
    public void addRightStatusBarHeightProvider(ResourceLocation resourceLocation, ToIntFunction<Player> heightProvider) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(heightProvider, "height provider is null");
        HudStatusBarHeightRegistry.addRight(resourceLocation, heightProvider);
    }
}
