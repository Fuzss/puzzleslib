package fuzs.puzzleslib.impl.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.api.client.event.v1.AddResourcePackReloadListenersCallback;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerNetworkEvents;
import fuzs.puzzleslib.api.client.gui.v2.GuiHeightHelper;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.client.config.ConfigTranslationsManager;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;

public class PuzzlesLibClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        AddResourcePackReloadListenersCallback.EVENT.register(ConfigTranslationsManager::onAddResourcePackReloadListeners);
        ClientPlayerNetworkEvents.LOGGED_OUT.register((LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection) -> {
            ModContext.clearPresentServerside();
        });
    }

    @Override
    public void onRegisterGuiLayers(GuiLayersContext context) {
        if (ModLoaderEnvironment.INSTANCE.getModLoader().isFabricLike()) {
            // offset text above hotbar depending on gui height values, NeoForge also does this
            context.replaceGuiLayer(GuiLayersContext.SELECTED_ITEM_NAME, (LayeredDraw.Layer layer) -> {
                return (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                    Gui gui = Minecraft.getInstance().gui;
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0.0F, 59.0F - Math.max(59.0F, GuiHeightHelper.getMaxHeight(gui)), 0.0F);
                    layer.render(guiGraphics, deltaTracker);
                    guiGraphics.pose().popPose();
                };
            });
            context.replaceGuiLayer(GuiLayersContext.OVERLAY_MESSAGE, (LayeredDraw.Layer layer) -> {
                return (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                    Gui gui = Minecraft.getInstance().gui;
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose()
                            .translate(0.0F,
                                    68.0F - Math.max(68.0F,
                                            GuiHeightHelper.getMaxHeight(gui) + gui.minecraft.font.lineHeight),
                                    0.0F);
                    layer.render(guiGraphics, deltaTracker);
                    guiGraphics.pose().popPose();
                };
            });
        }
    }
}
