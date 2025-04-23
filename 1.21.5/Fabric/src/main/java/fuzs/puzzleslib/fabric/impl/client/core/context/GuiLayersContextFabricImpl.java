package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.api.client.gui.v2.GuiHeightHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.rendering.v1.LayeredDrawerWrapper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

public final class GuiLayersContextFabricImpl implements GuiLayersContext {
    private static final Map<ResourceLocation, ResourceLocation> VANILLA_GUI_LAYERS = ImmutableMap.<ResourceLocation, ResourceLocation>builder()
            .put(CAMERA_OVERLAYS, IdentifiedLayer.MISC_OVERLAYS)
            .put(CROSSHAIR, IdentifiedLayer.CROSSHAIR)
            .put(HOTBAR, IdentifiedLayer.HOTBAR_AND_BARS)
            .put(JUMP_METER, IdentifiedLayer.HOTBAR_AND_BARS)
            .put(EXPERIENCE_BAR, IdentifiedLayer.HOTBAR_AND_BARS)
            .put(PLAYER_HEALTH, IdentifiedLayer.HOTBAR_AND_BARS)
            .put(ARMOR_LEVEL, IdentifiedLayer.HOTBAR_AND_BARS)
            .put(FOOD_LEVEL, IdentifiedLayer.HOTBAR_AND_BARS)
            .put(VEHICLE_HEALTH, IdentifiedLayer.HOTBAR_AND_BARS)
            .put(AIR_LEVEL, IdentifiedLayer.HOTBAR_AND_BARS)
            .put(SELECTED_ITEM_NAME, IdentifiedLayer.HOTBAR_AND_BARS)
            .put(EXPERIENCE_LEVEL, IdentifiedLayer.EXPERIENCE_LEVEL)
            .put(STATUS_EFFECTS, IdentifiedLayer.STATUS_EFFECTS)
            .put(BOSS_BAR, IdentifiedLayer.BOSS_BAR)
            .put(SLEEP_OVERLAY, IdentifiedLayer.SLEEP)
            .put(DEMO_TIMER, IdentifiedLayer.DEMO_TIMER)
            .put(DEBUG_OVERLAY, IdentifiedLayer.DEBUG)
            .put(SCOREBOARD, IdentifiedLayer.SCOREBOARD)
            .put(OVERLAY_MESSAGE, IdentifiedLayer.OVERLAY_MESSAGE)
            .put(TITLE, IdentifiedLayer.TITLE_AND_SUBTITLE)
            .put(CHAT, IdentifiedLayer.CHAT)
            .put(PLAYER_LIST, IdentifiedLayer.PLAYER_LIST)
            .put(SUBTITLES, IdentifiedLayer.SUBTITLES)
            .build();
    public static final Map<ResourceLocation, UnaryOperator<LayeredDraw.Layer>> REPLACED_GUI_LAYERS = new IdentityHashMap<>();

    @Override
    public void registerGuiLayer(ResourceLocation resourceLocation, LayeredDraw.Layer guiLayer) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(guiLayer, "gui layer is null");
        HudLayerRegistrationCallback.EVENT.register((LayeredDrawerWrapper layeredDrawerWrapper) -> {
            layeredDrawerWrapper.addLayer(IdentifiedLayer.of(resourceLocation, guiLayer));
        });
    }

    @Override
    public void registerGuiLayer(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, LayeredDraw.Layer guiLayer) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(otherResourceLocation, "other resource location is null");
        Objects.requireNonNull(guiLayer, "gui layer is null");
        if (VANILLA_GUI_LAYERS.containsKey(resourceLocation)) {
            ResourceLocation vanillaResourceLocation = VANILLA_GUI_LAYERS.get(resourceLocation);
            HudLayerRegistrationCallback.EVENT.register((LayeredDrawerWrapper layeredDrawerWrapper) -> {
                layeredDrawerWrapper.attachLayerAfter(vanillaResourceLocation, otherResourceLocation, guiLayer);
            });
        } else if (VANILLA_GUI_LAYERS.containsKey(otherResourceLocation)) {
            ResourceLocation vanillaResourceLocation = VANILLA_GUI_LAYERS.get(otherResourceLocation);
            HudLayerRegistrationCallback.EVENT.register((LayeredDrawerWrapper layeredDrawerWrapper) -> {
                layeredDrawerWrapper.attachLayerBefore(vanillaResourceLocation, resourceLocation, guiLayer);
            });
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
            if (vanillaResourceLocation != IdentifiedLayer.HOTBAR_AND_BARS) {
                HudLayerRegistrationCallback.EVENT.register((LayeredDrawerWrapper layeredDrawerWrapper) -> {
                    layeredDrawerWrapper.replaceLayer(vanillaResourceLocation, (IdentifiedLayer identifiedLayer) -> {
                        return IdentifiedLayer.of(identifiedLayer.id(), guiLayerFactory.apply(identifiedLayer));
                    });
                });
            } else {
                REPLACED_GUI_LAYERS.merge(resourceLocation,
                        guiLayerFactory,
                        (UnaryOperator<LayeredDraw.Layer> originalGuiLayerFactory, UnaryOperator<LayeredDraw.Layer> newGuiLayerFactory) -> {
                            return (LayeredDraw.Layer layer) -> {
                                return newGuiLayerFactory.apply(originalGuiLayerFactory.apply(layer));
                            };
                        });
            }
        } else {
            throw new RuntimeException("Unregistered gui layer: " + resourceLocation);
        }
    }

    public static void renderGuiLayer(ResourceLocation resourceLocation, GuiGraphics guiGraphics, DeltaTracker deltaTracker, Runnable runnable) {
        UnaryOperator<LayeredDraw.Layer> unaryOperator = REPLACED_GUI_LAYERS.get(resourceLocation);
        if (unaryOperator != null) {
            unaryOperator.apply((GuiGraphics guiGraphicsX, DeltaTracker deltaTrackerX) -> {
                runnable.run();
            }).render(guiGraphics, deltaTracker);
        } else {
            runnable.run();
        }
    }

    public static void applyPlayerHealthGuiHeight(Gui gui) {
        if (gui.minecraft.gameMode.canHurtPlayer() && gui.minecraft.getCameraEntity() instanceof Player player) {
            int playerHealth = Mth.ceil(player.getHealth());
            float maxHealth = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH),
                    (float) Math.max(gui.displayHealth, playerHealth));
            int absorptionAmount = Mth.ceil(player.getAbsorptionAmount());
            int healthRows = Mth.ceil((maxHealth + (float) absorptionAmount) / 2.0F / 10.0F);
            int healthRowShift = Math.max(10 - (healthRows - 2), 3);
            GuiHeightHelper.addLeftHeight(gui, 10 + (healthRows - 1) * healthRowShift);
        }
    }

    public static void applyArmorLevelGuiHeight(Gui gui) {
        if (gui.minecraft.gameMode.canHurtPlayer() && gui.minecraft.getCameraEntity() instanceof Player player &&
                player.getArmorValue() > 0) {
            GuiHeightHelper.addLeftHeight(gui, 10);
        }
    }

    public static void applyFoodLevelGuiHeight(Gui gui) {
        if (gui.minecraft.gameMode.canHurtPlayer() && gui.minecraft.getCameraEntity() instanceof Player) {
            LivingEntity livingEntity = gui.getPlayerVehicleWithHealth();
            if (gui.getVehicleMaxHearts(livingEntity) == 0) {
                GuiHeightHelper.addRightHeight(gui, 10);
            }
        }
    }

    public static void applyAirLevelGuiHeight(Gui gui) {
        if (gui.minecraft.gameMode.canHurtPlayer() && gui.minecraft.getCameraEntity() instanceof Player player) {
            int maxAirSupply = player.getMaxAirSupply();
            int airSupply = Math.min(player.getAirSupply(), maxAirSupply);
            if (player.isEyeInFluid(FluidTags.WATER) || airSupply < maxAirSupply) {
                GuiHeightHelper.addRightHeight(gui, 10);
            }
        }
    }

    public static void applyVehicleHealthGuiHeight(Gui gui) {
        if (gui.minecraft.getCameraEntity() instanceof Player) {
            LivingEntity livingEntity = gui.getPlayerVehicleWithHealth();
            int maxHearts = gui.getVehicleMaxHearts(livingEntity);
            GuiHeightHelper.addRightHeight(gui, 10 * Mth.ceil(maxHearts / 10.0F));
        }
    }
}
