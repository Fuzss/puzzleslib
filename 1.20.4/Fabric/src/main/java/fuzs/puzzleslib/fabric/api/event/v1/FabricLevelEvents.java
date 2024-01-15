package fuzs.puzzleslib.fabric.api.event.v1;

import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import fuzs.puzzleslib.api.event.v1.level.*;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.world.level.Explosion;

/**
 * Events originally found on Forge in the <code>net.minecraftforge.event.level</code> package.
 */
public final class FabricLevelEvents {
    /**
     * Fires when a block is about to drop experience after being broken.
     */
    public static final Event<BlockEvents.DropExperience> DROP_BLOCK_EXPERIENCE = FabricEventFactory.create(BlockEvents.DropExperience.class);
    /**
     * Fired when an entity falls onto a block of farmland and in the process would trample on it, turning the block into dirt and destroying potential crops.
     */
    public static final Event<BlockEvents.FarmlandTrample> FARMLAND_TRAMPLE = FabricEventFactory.createResult(BlockEvents.FarmlandTrample.class);
    /**
     * Called just before an {@link Explosion} is about to be executed for a level, allows for preventing that explosion.
     */
    public static final Event<ExplosionEvents.Start> EXPLOSION_START = FabricEventFactory.createResult(ExplosionEvents.Start.class);
    /**
     * Called just before entities affected by an ongoing explosion are processed, meaning before they are hurt and knocked back.
     */
    public static final Event<ExplosionEvents.Detonate> EXPLOSION_DETONATE = FabricEventFactory.create(ExplosionEvents.Detonate.class);
    /**
     * Called when a sound event is played at a specific position in the world, allows for cancelling the sound.
     */
    public static final Event<PlayLevelSoundEvents.AtPosition> PLAY_LEVEL_SOUND_AT_POSITION = FabricEventFactory.createResult(PlayLevelSoundEvents.AtPosition.class);
    /**
     * Called when a sound event is played at a specific entity, allows for cancelling the sound.
     */
    public static final Event<PlayLevelSoundEvents.AtEntity> PLAY_LEVEL_SOUND_AT_ENTITY = FabricEventFactory.createResult(PlayLevelSoundEvents.AtEntity.class);
    /**
     * Fires when building a list of all possible entities that can spawn at the specified location.
     */
    public static final Event<GatherPotentialSpawnsCallback> GATHER_POTENTIAL_SPAWNS = FabricEventFactory.create(GatherPotentialSpawnsCallback.class);
    /**
     * Fires when a server player begins watching a chunk, and it has just been sent to a client.
     */
    public static final Event<ServerChunkEvents.Watch> WATCH_CHUNK = FabricEventFactory.create(ServerChunkEvents.Watch.class);
    /**
     * Fires when a server player stops watching a chunk.
     */
    public static final Event<ServerChunkEvents.Unwatch> UNWATCH_CHUNK = FabricEventFactory.create(ServerChunkEvents.Unwatch.class);

    private FabricLevelEvents() {

    }
}
