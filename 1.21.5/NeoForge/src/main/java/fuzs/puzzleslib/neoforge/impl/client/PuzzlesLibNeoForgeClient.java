package fuzs.puzzleslib.neoforge.impl.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.client.PuzzlesLibClient;
import fuzs.puzzleslib.impl.content.client.PuzzlesLibClientDevelopment;
import fuzs.puzzleslib.neoforge.mixin.client.accessor.RegisterKeyMappingsEventNeoForgeAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = PuzzlesLib.MOD_ID, dist = Dist.CLIENT)
public class PuzzlesLibNeoForgeClient {

    public PuzzlesLibNeoForgeClient(ModContainer modContainer) {
        ClientModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibClient::new);
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) {
            ClientModConstructor.construct(PuzzlesLibMod.id("development"), PuzzlesLibClientDevelopment::new);
        }
        registerLoadingHandlers(modContainer.getEventBus());
        registerEventHandlers(NeoForge.EVENT_BUS);
    }

    private static void registerLoadingHandlers(IEventBus eventBus) {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) return;
        eventBus.addListener((final RegisterKeyMappingsEvent evt) -> {
            Options options = ((RegisterKeyMappingsEventNeoForgeAccessor) evt).puzzleslib$getOptions();
            // hijack the event, it fires at the perfect time for us to manipulate game options before the file can be written
            PuzzlesLibClientDevelopment.setupGameOptions(options);
        });
    }

    private static void registerEventHandlers(IEventBus eventBus) {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) return;
        eventBus.addListener((final RenderGuiLayerEvent.Pre evt) -> {
            if (evt.getName().equals(VanillaGuiLayers.TAB_LIST)) {
                evt.setCanceled(true);
                Minecraft minecraft = Minecraft.getInstance();
                Scoreboard scoreboard = minecraft.level.getScoreboard();
                Objective objective = scoreboard.getDisplayObjective(DisplaySlot.LIST);
                if (minecraft.options.keyPlayerList.isDown() && minecraft.isLocalServer() &&
                        minecraft.player.connection.getListedOnlinePlayers().size() <= 1 && objective == null) {
                    minecraft.gui.tabList.setVisible(true);
                    minecraft.gui.tabList.render(evt.getGuiGraphics(),
                            evt.getGuiGraphics().guiWidth(),
                            scoreboard,
                            objective);
                } else {
                    evt.getLayer().render(evt.getGuiGraphics(), evt.getPartialTick());
                }
            }
        });
    }
}
