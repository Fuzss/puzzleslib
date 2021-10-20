package fuzs.puzzleslib.client;

import fuzs.puzzleslib.PuzzlesLib;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

//@Mod.EventBusSubscriber(modid = PuzzlesLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PuzzlesLibClient {

    @SubscribeEvent
    public void onConstructMod(final FMLConstructModEvent evt) {
    }

    @SubscribeEvent
    public void onClientSetup(final FMLClientSetupEvent evt) {
    }

    @SubscribeEvent
    public void onModelRegistry(final ModelRegistryEvent evt) {
    }

    @SubscribeEvent
    public void onTextureStitch(final TextureStitchEvent.Pre evt) {
    }
}
