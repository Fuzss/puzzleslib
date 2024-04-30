package fuzs.puzzleslib.forge.api.core.v1;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

import java.util.Objects;
import java.util.Optional;

/**
 * Small helper methods for mod containers and the corresponding mod event bus on Forge.
 */
public final class ForgeModContainerHelper {

    private ForgeModContainerHelper() {
        // NO-OP
    }

    /**
     * Find the active {@link IEventBus} based on {@link net.neoforged.fml.ModLoadingContext#get()}.
     *
     * @return the active mod event bus
     */
    public static IEventBus getActiveModEventBus() {
        return getOptionalActiveModEventBus().orElseThrow(() -> new NullPointerException("mod event bus is null"));
    }

    /**
     * Find the active {@link IEventBus} based on {@link net.neoforged.fml.ModLoadingContext#get()}.
     *
     * @return the active mod event bus
     */
    public static Optional<IEventBus> getOptionalActiveModEventBus() {
        String activeNamespace = ModLoadingContext.get().getActiveNamespace();
        // filter out minecraft, which is the default mod container returned when none is currently set as active
        if (activeNamespace.equals("minecraft")) {
            return Optional.empty();
        } else {
            return getOptionalModEventBus(activeNamespace);
        }
    }

    /**
     * Find the {@link IEventBus} for a specified <code>modId</code>.
     * <p>
     * Be careful with this, the mod event bus is not available e.g. when mod loading has failed due to unmet mod
     * dependencies, so we don't want to crash then so Forge can show the proper screen informing the user.
     *
     * @param modId id for mod container
     * @return the mod event bus
     */
    public static IEventBus getModEventBus(String modId) {
        return getOptionalModEventBus(modId).orElseThrow(() -> new NullPointerException("mod event bus for %s is null".formatted(
                modId)));
    }

    /**
     * Find the {@link IEventBus} for a specified <code>modId</code>.
     *
     * @param modId id for mod container
     * @return the mod event bus
     */
    public static Optional<IEventBus> getOptionalModEventBus(String modId) {
        return getOptionalModContainer(modId).filter(FMLModContainer.class::isInstance)
                .map(FMLModContainer.class::cast)
                .map(FMLModContainer::getEventBus);
    }

    /**
     * Find the {@link ModContainer} for a specified <code>modId</code>.
     * <p>
     * Be careful with this, the mod container will not be found when this is called too early and the mod list has not
     * been initialized.
     *
     * @param modId id for mod container
     * @return the mod container
     */
    public static ModContainer getModContainer(String modId) {
        return getOptionalModContainer(modId).orElseThrow(() -> new NullPointerException("mod container for %s is null".formatted(
                modId)));
    }

    /**
     * Find the {@link ModContainer} for a specific <code>modId</code>.
     *
     * @param modId id for mod container
     * @return the mod container
     */
    public static Optional<? extends ModContainer> getOptionalModContainer(String modId) {
        ModList modList = ModList.get();
        Objects.requireNonNull(modList, "mod list is null");
        return modList.getModContainerById(modId);
    }
}
