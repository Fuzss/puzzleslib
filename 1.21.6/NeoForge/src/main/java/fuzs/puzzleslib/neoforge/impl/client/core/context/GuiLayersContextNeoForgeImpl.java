package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.neoforge.mixin.client.accessor.RegisterGuiLayersEventAccessor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public final class GuiLayersContextNeoForgeImpl implements GuiLayersContext {
    private static final Map<ResourceLocation, ResourceLocation> VANILLA_GUI_LAYERS = ImmutableMap.<ResourceLocation, ResourceLocation>builder()
            .put(CAMERA_OVERLAYS, VanillaGuiLayers.CAMERA_OVERLAYS)
            .put(CROSSHAIR, VanillaGuiLayers.CROSSHAIR)
            .put(HOTBAR, VanillaGuiLayers.HOTBAR)
            .put(INFO_BAR, VanillaGuiLayers.CONTEXTUAL_INFO_BAR)
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
            .put(DEBUG_OVERLAY, VanillaGuiLayers.DEBUG_OVERLAY)
            .put(SCOREBOARD, VanillaGuiLayers.SCOREBOARD_SIDEBAR)
            .put(OVERLAY_MESSAGE, VanillaGuiLayers.OVERLAY_MESSAGE)
            .put(TITLE, VanillaGuiLayers.TITLE)
            .put(CHAT, VanillaGuiLayers.CHAT)
            .put(PLAYER_LIST, VanillaGuiLayers.TAB_LIST)
            .put(SUBTITLES, VanillaGuiLayers.SUBTITLE_OVERLAY)
            .build();

    private final IEventBus eventBus;
    private final Collection<Consumer<RegisterGuiLayersEvent>> eventConsumers = new ArrayList<>();

    public GuiLayersContextNeoForgeImpl(IEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void registerGuiLayer(ResourceLocation resourceLocation, GuiLayersContext.Layer guiLayer) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(guiLayer, "gui layer is null");
        this.registerGuiLayer((RegisterGuiLayersEvent evt) -> evt.registerAboveAll(resourceLocation, guiLayer::render));
    }

    @Override
    public void registerGuiLayer(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, GuiLayersContext.Layer guiLayer) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(otherResourceLocation, "other resource location is null");
        Objects.requireNonNull(guiLayer, "gui layer is null");
        if (VANILLA_GUI_LAYERS.containsKey(resourceLocation)) {
            this.registerGuiLayer((RegisterGuiLayersEvent evt) -> evt.registerAbove(VANILLA_GUI_LAYERS.get(
                    resourceLocation), otherResourceLocation, guiLayer::render));
        } else if (VANILLA_GUI_LAYERS.containsKey(otherResourceLocation)) {
            this.registerGuiLayer((RegisterGuiLayersEvent evt) -> evt.registerBelow(VANILLA_GUI_LAYERS.get(
                    otherResourceLocation), resourceLocation, guiLayer::render));
        } else {
            throw new RuntimeException("Unregistered gui layers: " + resourceLocation + ", " + otherResourceLocation);
        }
    }

    @Override
    public void replaceGuiLayer(ResourceLocation resourceLocation, UnaryOperator<GuiLayersContext.Layer> guiLayerFactory) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(guiLayerFactory, "gui layer factory is null");
        if (VANILLA_GUI_LAYERS.containsKey(resourceLocation)) {
            ResourceLocation vanillaResourceLocation = VANILLA_GUI_LAYERS.get(resourceLocation);
            this.registerGuiLayer((RegisterGuiLayersEvent evt) -> {
                ((RegisterGuiLayersEventAccessor) evt).puzzleslib$getLayers()
                        .replaceAll((GuiLayerManager.NamedLayer namedLayer) -> {
                            if (namedLayer.name().equals(vanillaResourceLocation)) {
                                return new GuiLayerManager.NamedLayer(namedLayer.name(),
                                        (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                                            // render condition is not inherited from the parent, add it back manually,
                                            // since all are known for vanilla layers
                                            if (namedLayer.name().equals(VanillaGuiLayers.SLEEP_OVERLAY) ||
                                                    !Minecraft.getInstance().options.hideGui) {
                                                guiLayerFactory.apply(namedLayer.layer()::render)
                                                        .render(guiGraphics, deltaTracker);
                                            }
                                        });
                            } else {
                                return namedLayer;
                            }
                        });
            });
        } else {
            throw new RuntimeException("Unregistered gui layer: " + resourceLocation);
        }
    }

    private void registerGuiLayer(Consumer<RegisterGuiLayersEvent> eventConsumer) {
        if (this.eventConsumers.isEmpty()) {
            this.eventBus.addListener((final RegisterGuiLayersEvent evt) -> {
                for (Consumer<RegisterGuiLayersEvent> consumer : this.eventConsumers) {
                    consumer.accept(evt);
                }
            });
        }
        this.eventConsumers.add(eventConsumer);
    }
}
