package fuzs.puzzleslib.api.core.v1;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

import java.util.Objects;
import java.util.Optional;

/**
 * Small helper methods for mod containers and the corresponding mod event bus on Forge.
 */
public final class ModContainerHelper {

    private ModContainerHelper() {

    }

    /**
     * Find the active {@link IEventBus} supplied via {@link FMLJavaModLoadingContext#get()}.
     *
     * @return the active mod event bus
     */
    public static IEventBus getActiveModEventBus() {
        return getOptionalActiveModEventBus().orElseThrow(() -> new NullPointerException("active event bus is null"));
    }

    /**
     * Find the active {@link IEventBus} supplied via {@link FMLJavaModLoadingContext#get()}.
     *
     * @return the active mod event bus
     */
    public static Optional<IEventBus> getOptionalActiveModEventBus() {
        return Optional.ofNullable(FMLJavaModLoadingContext.get()).map(FMLJavaModLoadingContext::getModEventBus);
    }

    /**
     * Find the {@link IEventBus} for a specific <code>modId</code>.
     * <p>This bypasses having to rely on {@link FMLJavaModLoadingContext#getModEventBus()} from {@link FMLJavaModLoadingContext#get()}.
     *
     * @param modId id for mod container
     * @return the mod event bus
     */
    public static IEventBus getModEventBus(String modId) {
        return getOptionalModEventBus(modId).orElseThrow(() -> new NullPointerException("event bus for %s is null".formatted(modId)));
    }

    /**
     * Find the {@link IEventBus} for a specific <code>modId</code>.
     * <p>This bypasses having to rely on {@link FMLJavaModLoadingContext#getModEventBus()} from {@link FMLJavaModLoadingContext#get()}.
     *
     * @param modId id for mod container
     * @return the mod event bus
     */
    public static Optional<IEventBus> getOptionalModEventBus(String modId) {
        return getOptionalModContainer(modId).filter(FMLModContainer.class::isInstance).map(FMLModContainer.class::cast).map(FMLModContainer::getEventBus);
    }

    /**
     * Find the {@link ModContainer} for a specific <code>modId</code>.
     * <p>This bypasses having to rely on {@link ModLoadingContext#get()}.
     *
     * @param modId id for mod container
     * @return the mod container
     */
    public static ModContainer getModContainer(String modId) {
        return getOptionalModContainer(modId).orElseThrow(() -> new NullPointerException("mod container for %s is null".formatted(modId)));
    }

    /**
     * Find the {@link ModContainer} for a specific <code>modId</code>.
     * <p>This bypasses having to rely on {@link ModLoadingContext#get()}.
     *
     * @param modId id for mod container
     * @return the mod container
     */
    public static Optional<? extends ModContainer> getOptionalModContainer(String modId) {
        ModList modList = ModList.get();
        Objects.requireNonNull(modList, "mod list is null");
        return modList.getModContainerById(modId);
    }

    @Deprecated(forRemoval = true)
    public static Optional<IEventBus> findModEventBus(String modId) {
        return getOptionalModEventBus(modId);
    }

    @Deprecated(forRemoval = true)
    public static ModContainer findModContainer(String modId) {
        return getModContainer(modId);
    }
}
