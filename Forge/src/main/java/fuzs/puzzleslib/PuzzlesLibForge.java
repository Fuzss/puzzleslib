package fuzs.puzzleslib;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

/**
 * also puzzles lib mod on Forge, only really need so it shows in the mods list
 */
@Mod(PuzzlesLib.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PuzzlesLibForge extends PuzzlesLib {

    /**
     * find the {@link IEventBus} for a specific <code>modId</code> so we do not have to rely on {@link FMLJavaModLoadingContext#get()#getModEventBus()}
     *
     * @param modId id for mod container
     * @return      the mod event bus
     */
    public static IEventBus getModEventBus(String modId) {
        return ModList.get().getModContainerById(modId)
                .map(container -> (FMLModContainer) container)
                .map(FMLModContainer::getEventBus)
                .orElseThrow();
    }
}
