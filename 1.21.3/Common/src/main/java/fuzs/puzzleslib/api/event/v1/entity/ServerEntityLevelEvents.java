package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public final class ServerEntityLevelEvents {
    public static final EventInvoker<Load> LOAD = EventInvoker.lookup(Load.class);
    public static final EventInvoker<Spawn> SPAWN = EventInvoker.lookup(Spawn.class);
    public static final EventInvoker<Unload> UNLOAD = EventInvoker.lookup(Unload.class);

    private ServerEntityLevelEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Load {

        /**
         * Fired when an entity is added to the level on the server, either when being loaded from chunk storage or when
         * just having been spawned in.
         *
         * @param entity      the entity that is being loaded
         * @param serverLevel the level the entity is loaded in
         * @return {@link EventResult#INTERRUPT} to prevent the entity from being added to the level, effectively
         *         discarding it, {@link EventResult#PASS} for the entity to be added normally
         */
        EventResult onEntityLoad(Entity entity, ServerLevel serverLevel);
    }

    @FunctionalInterface
    public interface Spawn {

        /**
         * Fired when an entity is added to the level on the server when it has just been spawned in.
         *
         * @param entity            the entity that is being spawned
         * @param serverLevel       the level the entity is spawned in
         * @param entitySpawnReason this provides the spawn type which has been captured in
         *                          {@link net.minecraft.world.entity.Mob#finalizeSpawn(ServerLevelAccessor,
         *                          DifficultyInstance, EntitySpawnReason, SpawnGroupData)} if it has been called,
         *                          otherwise
         *                          <code>null</code>
         * @return {@link EventResult#INTERRUPT} to prevent the entity from being added to the level, effectively
         *         discarding it, {@link EventResult#PASS} for the entity to be added normally
         */
        EventResult onEntitySpawn(Entity entity, ServerLevel serverLevel, @Nullable EntitySpawnReason entitySpawnReason);
    }

    @FunctionalInterface
    public interface Unload {

        /**
         * Fired when an entity is removed from the level on the server.
         *
         * @param entity      the entity that is being unloaded
         * @param serverLevel the level the entity is unloaded in
         */
        void onEntityUnload(Entity entity, ServerLevel serverLevel);
    }
}
