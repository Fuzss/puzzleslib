package fuzs.puzzleslib.neoforge.impl.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.PuzzlesLibClient;
import fuzs.puzzleslib.neoforge.mixin.client.accessor.RegisterKeyMappingsEventNeoForgeAccessor;
import net.minecraft.client.Minecraft;
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
        registerLoadingHandlers(modContainer.getEventBus());
    }

    private static void registerLoadingHandlers(IEventBus eventBus) {
        eventBus.addListener((final RegisterKeyMappingsEvent evt) -> {
            if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) {
                Options options = ((RegisterKeyMappingsEventNeoForgeAccessor) evt).puzzleslib$getOptions();
                PuzzlesLibClient.setupGameOptions(Minecraft.getInstance(), options);
            }
        });
    }
}
