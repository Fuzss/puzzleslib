package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public final class ServerEntityEvents {
    public static final EventInvoker<Join> JOIN = EventInvoker.lookup(Join.class);
    public static final EventInvoker<Load> LOAD = EventInvoker.lookup(Load.class);
    public static final EventInvoker<Unload> UNLOAD = EventInvoker.lookup(Unload.class);

    private ServerEntityEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Join {

        /**
         * Fired before an entity is added to the level on the server, allowing it to be prevented from loading in.
         * <p>
         * Accessing chunks here (e.g. {@link ServerLevelAccessor#getCurrentDifficultyAt(BlockPos)}) can deadlock the
         * game. Use {@link Load} instead when chunk access is needed.
         *
         * @param entity           the entity being added
         * @param serverLevel      the level the entity is added to
         * @param isLoadedFromDisk the entity was loaded from storage rather than spawned
         * @param spawnReason      the spawn reason, if captured
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} to discard the entity</li>
         *         <li>{@link EventResult#PASS PASS} to add it normally</li>
         *         </ul>
         */
        EventResult onEntityJoin(Entity entity, ServerLevel serverLevel, boolean isLoadedFromDisk, @Nullable MobSpawnType spawnReason);
    }

    @FunctionalInterface
    public interface Load {

        /**
         * Fired after an entity has been added to the level on the server in a state safe to modify.
         *
         * @param entity           the entity that was added
         * @param serverLevel      the level the entity was added to
         * @param isLoadedFromDisk the entity was loaded from storage rather than spawned
         * @param spawnReason      the spawn reason, if captured
         */
        void onEntityLoad(Entity entity, ServerLevel serverLevel, boolean isLoadedFromDisk, @Nullable MobSpawnType spawnReason);
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
