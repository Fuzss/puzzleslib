package fuzs.puzzleslib.neoforge.impl.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.PuzzlesLibClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = PuzzlesLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PuzzlesLibNeoForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibClient::new);
    }
}
