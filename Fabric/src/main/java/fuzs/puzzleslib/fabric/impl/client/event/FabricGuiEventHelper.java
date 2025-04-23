package fuzs.puzzleslib.fabric.impl.client.event;

import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
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
@Deprecated
public final class FabricGuiEventHelper {
    private static final Set<ResourceLocation> CANCELLED_GUI_LAYERS = new HashSet<>();

    private FabricGuiEventHelper() {
        // NO-OP
    }

    public static void registerEventHandlers() {
        RenderGuiEvents.BEFORE.register(EventPhase.FIRST,
                (Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                    CANCELLED_GUI_LAYERS.clear();
                });
        RenderGuiEvents.BEFORE.register(EventPhase.AFTER, FabricGuiEventHelper::invokeGuiLayerEvents);
    }

    private static void invokeGuiLayerEvents(Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (gui.minecraft.options.hideGui) return;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 50.0F);
        for (ResourceLocation resourceLocation : RenderGuiLayerEvents.VANILLA_GUI_LAYERS_VIEW) {
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
}
