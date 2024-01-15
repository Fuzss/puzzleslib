package fuzs.puzzleslib.api.capability.v2;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.capability.v2.data.*;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * class for registering and attaching mod capabilities, every mod gets their own instance,
 * mostly due to Forge requiring to register some events which should be done on a per-mod basis
 */
public interface CapabilityController {
    /**
     * all actual capabilities implemented by mod loader specific projects
     * duplicates shouldn't be able to happen as only one mod loader implementation can run at once adding its data
     */
    @ApiStatus.Internal
    Map<ResourceLocation, CapabilityKey<?, ?>> CAPABILITY_KEY_REGISTRY = Maps.newConcurrentMap();
    /**
     * Valid classes used internally for managing registered capabilities.
     */
    @ApiStatus.Internal
    Set<Class<?>> VALID_CAPABILITY_TYPES = Set.of(Entity.class, BlockEntity.class, LevelChunk.class, Level.class);

    /**
     * called from constructors of mod loader specific {@link CapabilityComponent} implementations
     * @param capabilityKey         a proper mod loader specific implementation of {@link CapabilityKey}
     * @param <C>                   capability type
     */
    @ApiStatus.Internal
    static <T, C extends CapabilityComponent<T>> void register(CapabilityKey<T, C> capabilityKey) {
        if (CAPABILITY_KEY_REGISTRY.put(capabilityKey.identifier(), capabilityKey) != null) {
            throw new IllegalStateException("Duplicate capability %s".formatted(capabilityKey.identifier()));
        }
    }

    @ApiStatus.Internal
    static CapabilityKey<?, ?> retrieve(ResourceLocation id) {
        CapabilityKey<?, ?> capabilityKey = CAPABILITY_KEY_REGISTRY.get(id);
        if (capabilityKey != null) {
            return capabilityKey;
        }
        throw new IllegalStateException("No capability registered for id %s".formatted(id));
    }

    /**
     * Creates a new capability controller for <code>namespace</code> or returns an existing one.
     *
     * @param modId namespace used for registration
     * @return the mod specific capability controller
     */
    static CapabilityController from(String modId) {
        return ModContext.get(modId).getCapabilityController();
    }

    /**
     * Register capability to {@link Entity} objects.
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param entityType            entity class to match
     * @param <T>                   entity type
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    <T extends Entity, C extends CapabilityComponent<T>> EntityCapabilityKey<T, C> registerEntityCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory, Class<T> entityType);

    /**
     * Register capability to {@link BlockEntity} objects.
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param blockEntityType       block entity class to match
     * @param <T>                   block entity type
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    <T extends BlockEntity, C extends CapabilityComponent<T>> BlockEntityCapabilityKey<T, C> registerBlockEntityCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory, Class<T> blockEntityType);

    /**
     * Register capability to {@link LevelChunk} objects.
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    <C extends CapabilityComponent<LevelChunk>> LevelChunkCapabilityKey<C> registerLevelChunkCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory);

    /**
     * Register capability to {@link Level} objects.
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    <C extends CapabilityComponent<Level>> LevelCapabilityKey<C> registerLevelCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory);
}
