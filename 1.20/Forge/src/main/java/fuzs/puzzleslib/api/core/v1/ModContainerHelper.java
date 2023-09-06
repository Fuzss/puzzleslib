package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

import java.util.Objects;
import java.util.Optional;

/**
 * small helper methods for Forge
 */
public final class ModContainerHelper {

    /**
     * private constructor
     */
    private ModContainerHelper() {

    }

    /**
     * find the {@link IEventBus} for a specific <code>modId</code> so we do not have to rely on {@link FMLJavaModLoadingContext#getModEventBus()} from {@link FMLJavaModLoadingContext#get()}
     *
     * @param modId id for mod container
     * @return      the mod event bus
     */
    public static Optional<IEventBus> findModEventBus(String modId) {
        if (findModContainer(modId) instanceof FMLModContainer modContainer) {
            return Optional.of(modContainer.getEventBus());
        }
        PuzzlesLib.LOGGER.error("No mod event bus for id %s exists, cannot proceed mod loading".formatted(modId));
        return Optional.empty();
    }

    /**
     * find the {@link ModContainer} for a specific <code>modId</code> so we do not have to rely on {@link ModLoadingContext#get()}
     *
     * @param modId id for mod container
     * @return      the mod container
     */
    public static ModContainer findModContainer(String modId) {
        ModList modList = ModList.get();
        Objects.requireNonNull(modList, "mod list is null");
        return modList.getModContainerById(modId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No mod container for id %s exists", modId)));
    }
}
