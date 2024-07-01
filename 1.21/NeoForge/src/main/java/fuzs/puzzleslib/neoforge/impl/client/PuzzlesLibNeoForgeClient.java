package fuzs.puzzleslib.neoforge.impl.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.PuzzlesLibClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = PuzzlesLib.MOD_ID, dist = Dist.CLIENT)
public class PuzzlesLibNeoForgeClient {

    public PuzzlesLibNeoForgeClient(ModContainer modContainer) {
        ClientModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibClient::new);
    }
}
