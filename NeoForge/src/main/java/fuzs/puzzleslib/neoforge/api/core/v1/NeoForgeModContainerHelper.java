package fuzs.puzzleslib.neoforge.api.core.v1;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;

import java.util.Objects;
import java.util.Optional;

/**
 * Small helper methods for mod containers and the corresponding mod event bus on Forge.
 */
public final class NeoForgeModContainerHelper {

    private NeoForgeModContainerHelper() {
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
        return Optional.of(ModLoadingContext.get().getActiveContainer())
                // filter out minecraft, which is the default mod container returned when none is currently set as active
                .filter(modContainer -> !modContainer.getNamespace().equals("minecraft"))
                .map(ModContainer::getEventBus);
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
        return getOptionalModContainer(modId).map(ModContainer::getEventBus);
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
