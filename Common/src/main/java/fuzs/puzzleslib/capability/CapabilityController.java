package fuzs.puzzleslib.capability;

import fuzs.puzzleslib.capability.data.CapabilityComponent;
import fuzs.puzzleslib.capability.data.CapabilityFactory;
import fuzs.puzzleslib.capability.data.CapabilityKey;
import fuzs.puzzleslib.capability.data.PlayerRespawnStrategy;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.function.Predicate;

/**
 * class for registering and attaching mod capabilities, every mod gets their own instance,
 * mostly due to Forge requiring to register some events which should be done on a per mod basis
 */
public interface CapabilityController {

    /**
     * register capability to {@link ItemStack} objects
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory
     * @param item                  item to apply to
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    default <C extends CapabilityComponent> CapabilityKey<C> registerItemCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Item item) {
        return this.registerItemCapability(capabilityKey, capabilityType, capabilityFactory, o -> o == item);
    }

    /**
     * register capability to {@link ItemStack} objects
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param itemFilter            filter for item, can be used for matching class and instanceof
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    <C extends CapabilityComponent> CapabilityKey<C> registerItemCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Predicate<Item> itemFilter);

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
    <T extends Entity, C extends CapabilityComponent> CapabilityKey<C> registerEntityCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Class<T> entityType);

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
    <C extends CapabilityComponent> CapabilityKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, PlayerRespawnStrategy respawnStrategy);

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
    <T extends BlockEntity, C extends CapabilityComponent> CapabilityKey<C> registerBlockEntityCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Class<T> blockEntityType);

    /**
     * register capability to {@link LevelChunk} objects
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    <C extends CapabilityComponent> CapabilityKey<C> registerLevelChunkCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory);

    /**
     * register capability to {@link Level} objects
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    <C extends CapabilityComponent> CapabilityKey<C> registerLevelCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory);
}
