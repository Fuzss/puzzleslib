package fuzs.puzzleslib.neoforge.impl;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(PuzzlesLib.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PuzzlesLibForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibMod::new);
    }
}
