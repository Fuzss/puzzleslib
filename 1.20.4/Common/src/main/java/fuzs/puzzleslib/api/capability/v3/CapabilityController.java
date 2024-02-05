package fuzs.puzzleslib.api.capability.v3;

import fuzs.puzzleslib.api.capability.v3.data.*;
import fuzs.puzzleslib.impl.capability.GlobalCapabilityRegister;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Main class for managing the registration and attachment of capabilities components on a per-mod basis.
 */
public interface CapabilityController {

    /**
     * Creates a new capability controller for <code>modId</code> or returns an existing one.
     *
     * @param modId mod id used for registration
     * @return the mod specific capability controller
     */
    static CapabilityController from(String modId) {
        return ModContext.get(modId).getCapabilityController();
    }

    /**
     * Get a registered capability key. Will throw if identifier is invalid.
     *
     * @param identifier identifier used during registration
     * @return capability key
     */
    @NotNull
    static CapabilityKey<?, ?> get(ResourceLocation identifier) {
        return GlobalCapabilityRegister.get(identifier);
    }

    /**
     * Register capability to {@link Entity} objects.
     *
     * @param identifier        path for internal identifier, used for serialization
     * @param capabilityType    class type for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param entityType        entity class to match
     * @param <T>               entity type
     * @param <C>               capability component type
     * @return capability key
     */
    <T extends Entity, C extends CapabilityComponent<T>> EntityCapabilityKey.Mutable<T, C> registerEntityCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory, Class<T> entityType);

    /**
     * Register capability to {@link BlockEntity} objects.
     *
     * @param identifier        path for internal identifier, used for serialization
     * @param capabilityType    class type for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param blockEntityType   block entity class to match
     * @param <T>               block entity type
     * @param <C>               capability component type
     * @return capability key
     */
    <T extends BlockEntity, C extends CapabilityComponent<T>> BlockEntityCapabilityKey<T, C> registerBlockEntityCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory, Class<T> blockEntityType);

    /**
     * Register capability to {@link LevelChunk} objects.
     *
     * @param identifier        path for internal identifier, used for serialization
     * @param capabilityType    interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param <C>               capability component type
     * @return capability key
     */
    <C extends CapabilityComponent<LevelChunk>> LevelChunkCapabilityKey<C> registerLevelChunkCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory);

    /**
     * Register capability to {@link Level} objects.
     *
     * @param identifier        path for internal identifier, used for serialization
     * @param capabilityType    class type for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param <C>               capability component type
     * @return capability key
     */
    <C extends CapabilityComponent<Level>> LevelCapabilityKey<C> registerLevelCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory);
}
