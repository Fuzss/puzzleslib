package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

public record GuiLayersContextNeoForgeImpl(RegisterGuiLayersEvent event) implements GuiLayersContext {
    private static final Map<Identifier, Identifier> VANILLA_GUI_LAYERS = ImmutableMap.<Identifier, Identifier>builder()
            .put(CAMERA_OVERLAYS, VanillaGuiLayers.CAMERA_OVERLAYS)
            .put(CROSSHAIR, VanillaGuiLayers.CROSSHAIR)
            .put(HOTBAR, VanillaGuiLayers.HOTBAR)
            .put(INFO_BAR, VanillaGuiLayers.CONTEXTUAL_INFO_BAR_BACKGROUND)
            .put(PLAYER_HEALTH, VanillaGuiLayers.PLAYER_HEALTH)
            .put(ARMOR_LEVEL, VanillaGuiLayers.ARMOR_LEVEL)
            .put(FOOD_LEVEL, VanillaGuiLayers.FOOD_LEVEL)
            .put(VEHICLE_HEALTH, VanillaGuiLayers.VEHICLE_HEALTH)
            .put(AIR_LEVEL, VanillaGuiLayers.AIR_LEVEL)
            .put(HELD_ITEM_TOOLTIP, VanillaGuiLayers.SELECTED_ITEM_NAME)
            .put(EXPERIENCE_LEVEL, VanillaGuiLayers.EXPERIENCE_LEVEL)
            .put(SPECTATOR_TOOLTIP, VanillaGuiLayers.SPECTATOR_TOOLTIP)
            .put(STATUS_EFFECTS, VanillaGuiLayers.EFFECTS)
            .put(BOSS_BAR, VanillaGuiLayers.BOSS_OVERLAY)
            .put(SLEEP_OVERLAY, VanillaGuiLayers.SLEEP_OVERLAY)
            .put(DEMO_TIMER, VanillaGuiLayers.DEMO_OVERLAY)
            .put(SCOREBOARD, VanillaGuiLayers.SCOREBOARD_SIDEBAR)
            .put(OVERLAY_MESSAGE, VanillaGuiLayers.OVERLAY_MESSAGE)
            .put(TITLE, VanillaGuiLayers.TITLE)
            .put(CHAT, VanillaGuiLayers.CHAT)
            .put(PLAYER_LIST, VanillaGuiLayers.TAB_LIST)
            .put(SUBTITLES, VanillaGuiLayers.SUBTITLE_OVERLAY)
            .build();

    @Override
    public void registerGuiLayer(Identifier identifier, GuiLayersContext.Layer guiLayer) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(guiLayer, "gui layer is null");
        this.event.registerAboveAll(identifier, guiLayer::render);
    }

    @Override
    public void registerGuiLayer(Identifier identifier, Identifier otherResourceLocation, GuiLayersContext.Layer guiLayer) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(otherResourceLocation, "other identifier is null");
        Objects.requireNonNull(guiLayer, "gui layer is null");
        // only check for vanilla layers, it simplifies the implementation and is all we need
        if (VANILLA_GUI_LAYERS.containsKey(identifier)) {
            this.event.registerAbove(VANILLA_GUI_LAYERS.get(identifier), otherResourceLocation, guiLayer::render);
        } else if (VANILLA_GUI_LAYERS.containsKey(otherResourceLocation)) {
            this.event.registerBelow(VANILLA_GUI_LAYERS.get(otherResourceLocation), identifier, guiLayer::render);
        } else {
            throw new RuntimeException("Unknown gui layers: " + identifier + ", " + otherResourceLocation);
        }
    }

    @Override
    public void replaceGuiLayer(Identifier identifier, UnaryOperator<GuiLayersContext.Layer> guiLayerFactory) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(guiLayerFactory, "gui layer factory is null");
        // only check for vanilla layers, it simplifies the implementation and is all we need
        if (VANILLA_GUI_LAYERS.containsKey(identifier)) {
            identifier = VANILLA_GUI_LAYERS.get(identifier);
            boolean isSleepOverlay = identifier.equals(VanillaGuiLayers.SLEEP_OVERLAY);
            this.event.wrapLayer(identifier, (GuiLayer guiLayer) -> {
                return (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                    // render condition is not inherited from the parent, add it back manually,
                    // since all are known for vanilla layers
                    if (isSleepOverlay || !Minecraft.getInstance().options.hideGui) {
                        guiLayerFactory.apply(guiLayer::render).render(guiGraphics, deltaTracker);
                    }
                };
            });
        } else {
            throw new RuntimeException("Unknown gui layer: " + identifier);
        }
    }

    @Override
    public void addLeftStatusBarHeightProvider(Identifier identifier, ToIntFunction<Player> heightProvider) {
        this.addStatusBarHeight(identifier, heightProvider, (Gui gui, Integer height) -> {
            gui.leftHeight += height;
        });
    }

    @Override
    public void addRightStatusBarHeightProvider(Identifier identifier, ToIntFunction<Player> heightProvider) {
        this.addStatusBarHeight(identifier, heightProvider, (Gui gui, Integer height) -> {
            gui.rightHeight += height;
        });
    }

    private void addStatusBarHeight(Identifier identifier, ToIntFunction<Player> heightProvider, BiConsumer<Gui, Integer> heightConsumer) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(heightProvider, "height provider is null");
        NeoForge.EVENT_BUS.addListener((final RenderGuiLayerEvent.Post event) -> {
            if (event.getName().equals(identifier)) {
                Gui gui = Minecraft.getInstance().gui;
                if (gui.getCameraPlayer() != null) {
                    heightConsumer.accept(gui, heightProvider.applyAsInt(gui.getCameraPlayer()));
                }
            }
        });
    }
}
