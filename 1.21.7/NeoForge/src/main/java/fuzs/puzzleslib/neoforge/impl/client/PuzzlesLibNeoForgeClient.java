package fuzs.puzzleslib.neoforge.impl.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.client.PuzzlesLibClient;
import fuzs.puzzleslib.impl.content.client.PuzzlesLibClientDevelopment;
import fuzs.puzzleslib.neoforge.mixin.client.accessor.RegisterKeyMappingsEventNeoForgeAccessor;
import net.minecraft.client.Options;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(value = PuzzlesLib.MOD_ID, dist = Dist.CLIENT)
public class PuzzlesLibNeoForgeClient {

    public PuzzlesLibNeoForgeClient(ModContainer modContainer) {
        ClientModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibClient::new);
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) {
            ClientModConstructor.construct(PuzzlesLibMod.id("client/development"), PuzzlesLibClientDevelopment::new);
        }
        registerLoadingHandlers(modContainer.getEventBus());
    }

    private static void registerLoadingHandlers(IEventBus eventBus) {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) return;
        eventBus.addListener((final RegisterKeyMappingsEvent event) -> {
            Options options = ((RegisterKeyMappingsEventNeoForgeAccessor) event).puzzleslib$getOptions();
            // we hijack the event; it fires at the perfect time for us to manipulate game options before the file can be written
            PuzzlesLibClientDevelopment.setupGameOptions(options);
        });
    }
}
