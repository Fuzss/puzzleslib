package fuzs.puzzleslib.fabric.impl.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.client.PuzzlesLibClient;
import fuzs.puzzleslib.impl.content.client.PuzzlesLibClientDevelopment;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.rendering.v1.LayeredDrawerWrapper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class PuzzlesLibFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibClient::new);
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) {
            ClientModConstructor.construct(PuzzlesLibMod.id("development"), PuzzlesLibClientDevelopment::new);
        }
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) return;
        HudLayerRegistrationCallback.EVENT.register((LayeredDrawerWrapper layeredDrawerWrapper) -> {
            layeredDrawerWrapper.replaceLayer(IdentifiedLayer.PLAYER_LIST, (IdentifiedLayer identifiedLayer) -> {
                return IdentifiedLayer.of(identifiedLayer.id(),
                        (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                            Minecraft minecraft = Minecraft.getInstance();
                            Scoreboard scoreboard = minecraft.level.getScoreboard();
                            Objective objective = scoreboard.getDisplayObjective(DisplaySlot.LIST);
                            if (minecraft.options.keyPlayerList.isDown() && minecraft.isLocalServer() &&
                                    minecraft.player.connection.getListedOnlinePlayers().size() <= 1 &&
                                    objective == null) {
                                minecraft.gui.tabList.setVisible(true);
                                minecraft.gui.tabList.render(guiGraphics,
                                        guiGraphics.guiWidth(),
                                        scoreboard,
                                        objective);
                            } else {
                                identifiedLayer.render(guiGraphics, deltaTracker);
                            }
                        });
            });
        });
    }
}
