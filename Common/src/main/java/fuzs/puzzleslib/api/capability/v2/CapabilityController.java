package fuzs.puzzleslib.api.capability.v2;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.capability.v2.data.*;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.function.Function;

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
    Map<ResourceLocation, CapabilityKey<?>> CAPABILITY_KEY_REGISTRY = Maps.newConcurrentMap();

    /**
     * called from constructors of mod loader specific {@link CapabilityComponent} implementations
     * @param capabilityKey         a proper mod loader specific implementation of {@link CapabilityKey}
     * @param <T>                   capability type
     */
    @ApiStatus.Internal
    static <T extends CapabilityComponent> void submit(CapabilityKey<T> capabilityKey) {
        if (CAPABILITY_KEY_REGISTRY.put(capabilityKey.getId(), capabilityKey) != null) {
            throw new IllegalStateException("Duplicate capability %s".formatted(capabilityKey.getId()));
        }
    }

    @ApiStatus.Internal
    static CapabilityKey<?> retrieve(ResourceLocation id) {
        CapabilityKey<?> capabilityKey = CAPABILITY_KEY_REGISTRY.get(id);
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
     * register capability to {@link Entity} objects
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param entityType            entity class to match
     * @param <T>                   entity type
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    <T extends Entity, C extends CapabilityComponent> CapabilityKey<C> registerEntityCapability(String capabilityKey, Class<C> capabilityType, Function<T, C> capabilityFactory, Class<T> entityType);

    /**
     * register capability to {@link Entity} objects
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param respawnStrategy       strategy to use when returning from the end dimension or after dying
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    <C extends CapabilityComponent> PlayerCapabilityKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, Function<Player, C> capabilityFactory, PlayerRespawnStrategy respawnStrategy);

    /**
     * register capability to {@link Entity} objects
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param respawnStrategy       strategy to use when returning from the end dimension or after dying
     * @param syncStrategy          how this capability is synced to the remote, set to {@link SyncStrategy#MANUAL} by default
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    <C extends CapabilityComponent> PlayerCapabilityKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, Function<Player, C> capabilityFactory, PlayerRespawnStrategy respawnStrategy, SyncStrategy syncStrategy);

    /**
     * register capability to {@link BlockEntity} objects
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param blockEntityType       block entity class to match
     * @param <T>                   block entity type
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    <T extends BlockEntity, C extends CapabilityComponent> CapabilityKey<C> registerBlockEntityCapability(String capabilityKey, Class<C> capabilityType, Function<T, C> capabilityFactory, Class<T> blockEntityType);

    /**
     * register capability to {@link LevelChunk} objects
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    <C extends CapabilityComponent> CapabilityKey<C> registerLevelChunkCapability(String capabilityKey, Class<C> capabilityType, Function<ChunkAccess, C> capabilityFactory);

    /**
     * register capability to {@link Level} objects
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    <C extends CapabilityComponent> CapabilityKey<C> registerLevelCapability(String capabilityKey, Class<C> capabilityType, Function<Level, C> capabilityFactory);
}
