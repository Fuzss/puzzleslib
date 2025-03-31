package fuzs.puzzleslib.fabric.impl.client.event;

import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.client.gui.v2.GuiHeightHelper;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * The keys are to be used with Fabric's {@link net.fabricmc.loader.api.ObjectShare}.
 * <p>
 * Contains the current render height of hotbar decorations as an {@link Integer}.
 * <p>
 * When rendering additional hotbar decorations on the screen make sure to update the value by adding the height of the
 * decorations, which is usually {@code 10}.
 * <p>
 * The implementation is meant to be similar to NeoForge's {@code Gui#leftHeight} &amp; {@code Gui#rightHeight}.
 */
public final class FabricGuiEventHelper {
    private static final Set<ResourceLocation> CANCELLED_GUI_LAYERS = new HashSet<>();

    private FabricGuiEventHelper() {
        // NO-OP
    }

    private static void invokeGuiLayerEvents(Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (gui.minecraft.options.hideGui) return;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 50.0F);
        for (ResourceLocation resourceLocation : RenderGuiLayerEvents.VANILLA_GUI_LAYERS_VIEW) {
            resourceLocation = RenderGuiLayerEvents.getModLoaderGuiLayer(resourceLocation);
            if (FabricGuiEvents.beforeRenderGuiElement(resourceLocation)
                    .invoker()
                    .onBeforeRenderGuiLayer(gui, guiGraphics, deltaTracker)
                    .isInterrupt()) {
                CANCELLED_GUI_LAYERS.add(resourceLocation);
            } else {
                FabricGuiEvents.afterRenderGuiElement(resourceLocation)
                        .invoker()
                        .onAfterRenderGuiLayer(gui, guiGraphics, deltaTracker);
            }
            guiGraphics.pose().translate(0.0F, 0.0F, LayeredDraw.Z_SEPARATION);
        }
        guiGraphics.pose().popPose();
    }

    public static void cancelIfNecessary(ResourceLocation resourceLocation, CallbackInfo callback) {
        if (CANCELLED_GUI_LAYERS.contains(resourceLocation)) {
            callback.cancel();
        }
    }

    public static void registerEventHandlers() {
        RenderGuiEvents.BEFORE.register(EventPhase.FIRST,
                (Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                    CANCELLED_GUI_LAYERS.clear();
                    GuiHeightHelper.setLeftHeight(gui, 39);
                    GuiHeightHelper.setRightHeight(gui, 39);
                });
        RenderGuiEvents.BEFORE.register(EventPhase.AFTER, FabricGuiEventHelper::invokeGuiLayerEvents);
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.PLAYER_HEALTH)
                .register(EventPhase.FIRST, (Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                    if (gui.minecraft.gameMode.canHurtPlayer() &&
                            gui.minecraft.getCameraEntity() instanceof Player player) {
                        int playerHealth = Mth.ceil(player.getHealth());
                        float maxHealth = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH),
                                (float) Math.max(gui.displayHealth, playerHealth));
                        int absorptionAmount = Mth.ceil(player.getAbsorptionAmount());
                        int healthRows = Mth.ceil((maxHealth + (float) absorptionAmount) / 2.0F / 10.0F);
                        int healthRowShift = Math.max(10 - (healthRows - 2), 3);
                        GuiHeightHelper.addLeftHeight(gui, 10 + (healthRows - 1) * healthRowShift);
                    }
                });
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.ARMOR_LEVEL)
                .register(EventPhase.FIRST, (Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                    if (gui.minecraft.gameMode.canHurtPlayer() &&
                            gui.minecraft.getCameraEntity() instanceof Player player && player.getArmorValue() > 0) {
                        GuiHeightHelper.addLeftHeight(gui, 10);
                    }
                });
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.FOOD_LEVEL)
                .register(EventPhase.FIRST, (Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                    if (gui.minecraft.gameMode.canHurtPlayer() && gui.minecraft.getCameraEntity() instanceof Player) {
                        LivingEntity livingEntity = gui.getPlayerVehicleWithHealth();
                        if (gui.getVehicleMaxHearts(livingEntity) == 0) {
                            GuiHeightHelper.addRightHeight(gui, 10);
                        }
                    }
                });
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.AIR_LEVEL)
                .register(EventPhase.FIRST, (Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                    if (gui.minecraft.gameMode.canHurtPlayer() &&
                            gui.minecraft.getCameraEntity() instanceof Player player) {
                        int maxAirSupply = player.getMaxAirSupply();
                        int airSupply = Math.min(player.getAirSupply(), maxAirSupply);
                        if (player.isEyeInFluid(FluidTags.WATER) || airSupply < maxAirSupply) {
                            GuiHeightHelper.addRightHeight(gui, 10);
                        }
                    }
                });
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.VEHICLE_HEALTH)
                .register(EventPhase.FIRST, (Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                    if (gui.minecraft.getCameraEntity() instanceof Player) {
                        LivingEntity livingEntity = gui.getPlayerVehicleWithHealth();
                        int maxHearts = gui.getVehicleMaxHearts(livingEntity);
                        GuiHeightHelper.addRightHeight(gui, 10 * Mth.ceil(maxHearts / 10.0F));
                    }
                });
    }
}
