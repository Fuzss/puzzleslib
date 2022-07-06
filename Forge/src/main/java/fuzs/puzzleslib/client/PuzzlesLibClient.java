package fuzs.puzzleslib.client;

import fuzs.puzzleslib.PuzzlesLib;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

/**
 * main client class on Fabric
 */
@Mod.EventBusSubscriber(modid = PuzzlesLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PuzzlesLibClient {

}
