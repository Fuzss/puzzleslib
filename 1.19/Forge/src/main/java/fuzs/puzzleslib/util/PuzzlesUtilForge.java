package fuzs.puzzleslib.util;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

/**
 * small helper methods for Forge
 */
public class PuzzlesUtilForge {

    /**
     * find the {@link IEventBus} for a specific <code>modId</code> so we do not have to rely on {@link FMLJavaModLoadingContext#getModEventBus()} from {@link FMLJavaModLoadingContext#get()}
     *
     * @param modId id for mod container
     * @return      the mod event bus
     */
    public static IEventBus findModEventBus(String modId) {
        return ((FMLModContainer) findModContainer(modId)).getEventBus();
    }

    /**
     * find the {@link ModContainer} for a specific <code>modId</code> so we do not have to rely on {@link ModLoadingContext#get()}
     *
     * @param modId id for mod container
     * @return      the mod container
     */
    public static ModContainer findModContainer(String modId) {
        return ModList.get().getModContainerById(modId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No mod for id %s exists", modId)));
    }
}
