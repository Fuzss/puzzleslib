package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;

public final class ServerEntityLevelEvents {
    public static final EventInvoker<Load> LOAD = EventInvoker.lookup(Load.class);
    public static final EventInvoker<Unload> UNLOAD = EventInvoker.lookup(Unload.class);

    private ServerEntityLevelEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Load {

        /**
         * Fired when an entity is added to the level on the server, either when being loaded from chunk storage or when
         * just having been spawned in.
         * <p>
         * For newly spawned entities the {@link net.minecraft.world.entity.EntitySpawnReason} can be obtained via
         * {@link fuzs.puzzleslib.api.util.v1.EntityHelper#getMobSpawnReason(Entity)} if captured in
         * {@link Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, EntitySpawnReason, SpawnGroupData)}.
         *
         * @param entity         the entity that is being loaded
         * @param serverLevel    the level the entity is loaded in
         * @param isNewlySpawned the entity has just been spawned in instead of being loaded from storage
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the entity from being added to the level, effectively discarding it</li>
         *         <li>{@link EventResult#PASS PASS} for the entity to be added normally</li>
         *         </ul>
         */
        EventResult onEntityLoad(Entity entity, ServerLevel serverLevel, boolean isNewlySpawned);
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
