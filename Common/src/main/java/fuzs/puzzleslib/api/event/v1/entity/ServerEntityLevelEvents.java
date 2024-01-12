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
    public static final EventInvoker<Load> LOAD = EventInvoker.lookup(Load.class);
    public static final EventInvoker<Spawn> SPAWN = EventInvoker.lookup(Spawn.class);
    public static final EventInvoker<Unload> UNLOAD = EventInvoker.lookup(Unload.class);

    private ServerEntityLevelEvents() {

    }

    @FunctionalInterface
    public interface Load {

        /**
         * Fired when an entity is added to the level on the server, either when being loaded from chunk storage or when just having been spawned in.
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
         * Fired when an entity is added to the level on the server when it has just been spawned in.
         *
         * @param entity         the entity that is being spawned
         * @param level          the level the entity is spawned in
         * @param spawnType      this provides the spawn type which has been captured in {@link net.minecraft.world.entity.Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, MobSpawnType, SpawnGroupData, CompoundTag)} if it has been called, otherwise <code>null</code>
         * @return {@link EventResult#INTERRUPT} to prevent the entity from being added to the level, effectively discarding it,
         * {@link EventResult#PASS} for the entity to be added normally
         */
        EventResult onEntitySpawn(Entity entity, ServerLevel level, @Nullable MobSpawnType spawnType);
    }

    @FunctionalInterface
    public interface Unload {

        /**
         * Fired when an entity is removed from the level on the server.
         *
         * @param entity the entity that is being unloaded
         * @param level  the level the entity is unloaded in
         */
        void onEntityUnload(Entity entity, ServerLevel level);
    }
}
