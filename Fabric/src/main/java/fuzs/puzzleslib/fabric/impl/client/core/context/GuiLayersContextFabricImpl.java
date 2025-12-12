package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudStatusBarHeightRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.Objects;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

public final class GuiLayersContextFabricImpl implements GuiLayersContext {
    private static final Map<Identifier, Identifier> VANILLA_GUI_LAYERS = ImmutableMap.<Identifier, Identifier>builder()
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
            .put(SCOREBOARD, VanillaHudElements.SCOREBOARD)
            .put(OVERLAY_MESSAGE, VanillaHudElements.OVERLAY_MESSAGE)
            .put(TITLE, VanillaHudElements.TITLE_AND_SUBTITLE)
            .put(CHAT, VanillaHudElements.CHAT)
            .put(PLAYER_LIST, VanillaHudElements.PLAYER_LIST)
            .put(SUBTITLES, VanillaHudElements.SUBTITLES)
            .build();

    public static Identifier getVanillaGuiLayer(Identifier identifier) {
        return VANILLA_GUI_LAYERS.getOrDefault(identifier, identifier);
    }

    @Override
    public void registerGuiLayer(Identifier identifier, GuiLayersContext.Layer guiLayer) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(guiLayer, "gui layer is null");
        HudElementRegistry.addLast(identifier, guiLayer::render);
    }

    @Override
    public void registerGuiLayer(Identifier identifier, Identifier otherIdentifier, GuiLayersContext.Layer guiLayer) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(otherIdentifier, "other identifier is null");
        Objects.requireNonNull(guiLayer, "gui layer is null");
        // only check for vanilla layers, it simplifies the implementation and is all we need
        if (VANILLA_GUI_LAYERS.containsKey(identifier)) {
            HudElementRegistry.attachElementAfter(VANILLA_GUI_LAYERS.get(identifier),
                    otherIdentifier,
                    guiLayer::render);
        } else if (VANILLA_GUI_LAYERS.containsKey(otherIdentifier)) {
            HudElementRegistry.attachElementBefore(VANILLA_GUI_LAYERS.get(otherIdentifier),
                    identifier,
                    guiLayer::render);
        } else {
            throw new RuntimeException("Unknown gui layers: " + identifier + ", " + otherIdentifier);
        }
    }

    @Override
    public void replaceGuiLayer(Identifier identifier, UnaryOperator<GuiLayersContext.Layer> guiLayerFactory) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(guiLayerFactory, "gui layer factory is null");
        // only check for vanilla layers, it simplifies the implementation and is all we need
        if (VANILLA_GUI_LAYERS.containsKey(identifier)) {
            HudElementRegistry.replaceElement(VANILLA_GUI_LAYERS.get(identifier), (HudElement hudElement) -> {
                return guiLayerFactory.apply(hudElement::render)::render;
            });
        } else {
            throw new RuntimeException("Unknown gui layer: " + identifier);
        }
    }

    @Override
    public void addLeftStatusBarHeightProvider(Identifier identifier, ToIntFunction<Player> heightProvider) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(heightProvider, "height provider is null");
        identifier = getVanillaGuiLayer(identifier);
        HudStatusBarHeightRegistry.addLeft(identifier, heightProvider::applyAsInt);
    }

    @Override
    public void addRightStatusBarHeightProvider(Identifier identifier, ToIntFunction<Player> heightProvider) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(heightProvider, "height provider is null");
        identifier = getVanillaGuiLayer(identifier);
        HudStatusBarHeightRegistry.addRight(identifier, heightProvider::applyAsInt);
    }
}
