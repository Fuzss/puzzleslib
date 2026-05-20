package fuzs.puzzleslib.neoforge.impl.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.client.PuzzlesLibClient;
import fuzs.puzzleslib.impl.content.client.PuzzlesLibClientDevelopment;
import fuzs.puzzleslib.neoforge.impl.client.commands.NeoForgeConfigCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = PuzzlesLib.MOD_ID, dist = Dist.CLIENT)
public class PuzzlesLibNeoForgeClient {

    public PuzzlesLibNeoForgeClient() {
        ClientModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibClient::new);
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) {
            ClientModConstructor.construct(PuzzlesLibMod.id("client/development"), PuzzlesLibClientDevelopment::new);
        }

        registerEventHandlers(NeoForge.EVENT_BUS);
    }

    private static void registerEventHandlers(IEventBus eventBus) {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) {
            return;
        }

        eventBus.addListener((final RegisterClientCommandsEvent evt) -> {
            NeoForgeConfigCommand.register(evt.getDispatcher(),
                    (CommandSourceStack commandSourceStack, Component component) -> {
                        commandSourceStack.sendSuccess(() -> component, true);
                    });
        });
    }
}
