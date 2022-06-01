package fuzs.puzzleslib.capability;

import fuzs.puzzleslib.capability.data.CapabilityComponent;
import fuzs.puzzleslib.capability.data.PlayerRespawnStrategy;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.function.Predicate;

/**
 * helper object for registering and attaching mod capabilities, needs to be extended by every mod individually
 * this basically is the same as {@link fuzs.puzzleslib.registry.RegistryManager}
 */
public interface CapabilityController {

    /**
     * register capability to {@link ItemStack} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param item              item to apply capability to
     * @param token capability token required to get capability instance from capability manager
     * @param <C> capability type
     * @return capability instance from capability manager
     */
    <C extends CapabilityComponent> CapabilityKey<C> registerItemCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Item item, CapabilityToken<C> token);

    /**
     * register capability to {@link ItemStack} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param itemFilter filter for item, can be used for matching class and instanceof
     * @param token capability token required to get capability instance from capability manager
     * @param <C> capability type
     * @return capability instance from capability manager
     */
    <C extends CapabilityComponent> CapabilityKey<C> registerItemCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Predicate<Item> itemFilter, CapabilityToken<C> token);

    /**
     * register capability to {@link Entity} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param entityType        entity class to match
     * @param token capability token required to get capability instance from capability manager
     * @param <C> capability type
     * @return capability instance from capability manager
     */
    <C extends CapabilityComponent> CapabilityKey<C> registerEntityCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Class<? extends Entity> entityType, CapabilityToken<C> token);

    /**
     * register capability to {@link Entity} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param respawnStrategy   strategy to use when returning from the end dimension or after dying
     * @param token capability token required to get capability instance from capability manager
     * @param <C> capability type
     * @return capability instance from capability manager
     */
    <C extends CapabilityComponent> CapabilityKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, PlayerRespawnStrategy respawnStrategy, CapabilityToken<C> token);

    /**
     * register capability to {@link BlockEntity} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param blockEntityType   block entity class to match
     * @param token capability token required to get capability instance from capability manager
     * @return capability instance from capability manager
     * @param <C> capability type
     */
    <C extends CapabilityComponent> CapabilityKey<C> registerBlockEntityCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Class<? extends BlockEntity> blockEntityType, CapabilityToken<C> token);

    /**
     * register capability to {@link LevelChunk} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param token capability token required to get capability instance from capability manager
     * @param <C> capability type
     * @return capability instance from capability manager
     */
    <C extends CapabilityComponent> CapabilityKey<C> registerLevelChunkCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, CapabilityToken<C> token);

    /**
     * register capability to {@link Level} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param token capability token required to get capability instance from capability manager
     * @param <C> capability type
     * @return capability instance from capability manager
     */
    <C extends CapabilityComponent> CapabilityKey<C> registerLevelCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, CapabilityToken<C> token);

    /**
     * helper interface for creating capability factories
     * @param <C> serializable capability type
     */
    @FunctionalInterface
    interface CapabilityFactory<C extends CapabilityComponent> {

        /**
         * @param t object to create capability from, mostly unused
         * @return the capability component
         */
        C createComponent(Object t);
    }
}
