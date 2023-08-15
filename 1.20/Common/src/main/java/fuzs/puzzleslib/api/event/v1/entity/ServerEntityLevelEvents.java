package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public final class ServerEntityLevelEvents {
    @Deprecated(forRemoval = true)
    public static final EventInvoker<Load> LOAD = EventInvoker.lookup(Load.class);
    public static final EventInvoker<LoadV2> LOAD_V2 = EventInvoker.lookup(LoadV2.class);
    public static final EventInvoker<Spawn> SPAWN = EventInvoker.lookup(Spawn.class);
    public static final EventInvoker<Remove> REMOVE = EventInvoker.lookup(Remove.class);

    private ServerEntityLevelEvents() {

    }

    @FunctionalInterface
    @Deprecated(forRemoval = true)
    public interface Load {

        /**
         * Fired when an entity is added to the level on the server.
         *
         * <p>In case the entity was added because it has just been spawned in the level (as opposed to being loaded from saved data),
         * instances of {@link net.minecraft.world.entity.Mob} will additionally provide the {@link MobSpawnType},
         * which essentially allows for preventing certain types of spawns.
         *
         * @param entity    the entity that is being loaded
         * @param level     the level the entity is loaded in
         * @param spawnType if a {@link net.minecraft.world.entity.Mob} was just spawned this provides the spawn type
         * @return {@link EventResult#INTERRUPT} to prevent the entity from being added to the level (on Fabric the entity will instead just immediately be removed again),
         * {@link EventResult#PASS} for the entity to be added normally
         */
        EventResult onEntityLoad(Entity entity, ServerLevel level, @Nullable MobSpawnType spawnType);
    }

    @FunctionalInterface
    public interface LoadV2 {

        /**
         * Fired when an entity is added to the level on the server after it has been loaded from chunk storage.
         *
         * @param entity         the entity that is being loaded
         * @param level          the level the entity is loaded in
         * @return {@link EventResult#INTERRUPT} to prevent the entity from being added to the level, effectively discarding it,
         * {@link EventResult#PASS} for the entity to be added normally
         */
        EventResult onEntityLoad(Entity entity, ServerLevel level);
    }

    @FunctionalInterface
    public interface Spawn {

        /**
         * Fired when an entity is added to the level on the server after it has just been spawned in.
         *
         * @param entity         the entity that is being spawned
         * @param level          the level the entity is spawned in
         * @param spawnType      this provides the spawn type which has been set in {@link net.minecraft.world.entity.Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, MobSpawnType, SpawnGroupData, CompoundTag)} if it has been called, otherwise <code>null</code>
         * @return {@link EventResult#INTERRUPT} to prevent the entity from being added to the level, effectively discarding it,
         * {@link EventResult#PASS} for the entity to be added normally
         */
        EventResult onEntitySpawn(Entity entity, ServerLevel level, @Nullable MobSpawnType spawnType);
    }

    @FunctionalInterface
    public interface Remove {

        /**
         * Fired when an entity is removed from the level on the server.
         *
         * @param entity the entity that is being unloaded
         * @param level  the level the entity is unloaded in
         */
        void onEntityRemove(Entity entity, ServerLevel level);
    }
}
