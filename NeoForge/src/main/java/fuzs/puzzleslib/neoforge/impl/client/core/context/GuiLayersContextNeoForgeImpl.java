package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

public record GuiLayersContextNeoForgeImpl(RegisterGuiLayersEvent evt) implements GuiLayersContext {
    private static final Map<ResourceLocation, ResourceLocation> VANILLA_GUI_LAYERS = ImmutableMap.<ResourceLocation, ResourceLocation>builder()
            .put(CAMERA_OVERLAYS, VanillaGuiLayers.CAMERA_OVERLAYS)
            .put(CROSSHAIR, VanillaGuiLayers.CROSSHAIR)
            .put(HOTBAR, VanillaGuiLayers.HOTBAR)
            .put(JUMP_METER, VanillaGuiLayers.JUMP_METER)
            .put(EXPERIENCE_BAR, VanillaGuiLayers.EXPERIENCE_BAR)
            .put(PLAYER_HEALTH, VanillaGuiLayers.PLAYER_HEALTH)
            .put(ARMOR_LEVEL, VanillaGuiLayers.ARMOR_LEVEL)
            .put(FOOD_LEVEL, VanillaGuiLayers.FOOD_LEVEL)
            .put(VEHICLE_HEALTH, VanillaGuiLayers.VEHICLE_HEALTH)
            .put(AIR_LEVEL, VanillaGuiLayers.AIR_LEVEL)
            .put(SELECTED_ITEM_NAME, VanillaGuiLayers.SELECTED_ITEM_NAME)
            .put(EXPERIENCE_LEVEL, VanillaGuiLayers.EXPERIENCE_LEVEL)
            .put(STATUS_EFFECTS, VanillaGuiLayers.EFFECTS)
            .put(BOSS_BAR, VanillaGuiLayers.BOSS_OVERLAY)
            .put(SLEEP_OVERLAY, VanillaGuiLayers.SLEEP_OVERLAY)
            .put(DEMO_TIMER, VanillaGuiLayers.DEMO_OVERLAY)
            .put(DEBUG_OVERLAY, VanillaGuiLayers.DEBUG_OVERLAY)
            .put(SCOREBOARD, VanillaGuiLayers.SCOREBOARD_SIDEBAR)
            .put(OVERLAY_MESSAGE, VanillaGuiLayers.OVERLAY_MESSAGE)
            .put(TITLE, VanillaGuiLayers.TITLE)
            .put(CHAT, VanillaGuiLayers.CHAT)
            .put(PLAYER_LIST, VanillaGuiLayers.TAB_LIST)
            .put(SUBTITLES, VanillaGuiLayers.SUBTITLE_OVERLAY)
            .build();

    @Override
    public void registerGuiLayer(ResourceLocation resourceLocation, LayeredDraw.Layer guiLayer) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(guiLayer, "gui layer is null");
        this.evt.registerAboveAll(resourceLocation, guiLayer);
    }

    @Override
    public void registerGuiLayer(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, LayeredDraw.Layer guiLayer) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(otherResourceLocation, "other resource location is null");
        Objects.requireNonNull(guiLayer, "gui layer is null");
        if (VANILLA_GUI_LAYERS.containsKey(resourceLocation)) {
            this.evt.registerAbove(VANILLA_GUI_LAYERS.get(resourceLocation), otherResourceLocation, guiLayer);
        } else if (VANILLA_GUI_LAYERS.containsKey(otherResourceLocation)) {
            this.evt.registerBelow(VANILLA_GUI_LAYERS.get(otherResourceLocation), resourceLocation, guiLayer);
        } else {
            throw new RuntimeException("Unregistered gui layers: " + resourceLocation + ", " + otherResourceLocation);
        }
    }

    @Override
    public void replaceGuiLayer(ResourceLocation resourceLocation, UnaryOperator<LayeredDraw.Layer> guiLayerFactory) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(guiLayerFactory, "gui layer factory is null");
        if (VANILLA_GUI_LAYERS.containsKey(resourceLocation)) {
            ResourceLocation vanillaResourceLocation = VANILLA_GUI_LAYERS.get(resourceLocation);
            NeoForge.EVENT_BUS.addListener((final RenderGuiLayerEvent.Pre evt) -> {
                if (evt.getName().equals(vanillaResourceLocation)) {
                    guiLayerFactory.apply(evt.getLayer()).render(evt.getGuiGraphics(), evt.getPartialTick());
                    evt.setCanceled(true);
                }
            });
        } else {
            throw new RuntimeException("Unregistered gui layer: " + resourceLocation);
        }
    }
}
