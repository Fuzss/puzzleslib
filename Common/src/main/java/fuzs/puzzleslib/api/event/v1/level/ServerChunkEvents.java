package fuzs.puzzleslib.api.event.v1.level;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

public final class ServerChunkEvents {
    public static final EventInvoker<Load> LOAD = EventInvoker.lookup(Load.class);
    public static final EventInvoker<Unload> UNLOAD = EventInvoker.lookup(Unload.class);
    public static final EventInvoker<Watch> WATCH = EventInvoker.lookup(Watch.class);
    public static final EventInvoker<Unwatch> UNWATCH = EventInvoker.lookup(Unwatch.class);

    private ServerChunkEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Load {

        /**
         * Fires before a server chunk is loaded.
         *
         * @param serverLevel the server level the chunk is in
         * @param levelChunk  the chunk being loaded
         */
        void onChunkLoad(ServerLevel serverLevel, LevelChunk levelChunk);
    }

    @FunctionalInterface
    public interface Unload {

        /**
         * Fires before a server chunk is unloaded.
         *
         * @param serverLevel the server level the chunk is in
         * @param levelChunk  the chunk being unloaded
         */
        void onChunkUnload(ServerLevel serverLevel, LevelChunk levelChunk);
    }

    @FunctionalInterface
    public interface Watch {

        /**
         * Fires when a server player begins watching a chunk, and it has just been sent to a client.
         * <p>Useful for syncing additional chunk data.
         *
         * @param serverPlayer the player watching the chunk
         * @param levelChunk   the chunk
         * @param serverLevel  the level the chunk is in
         */
        void onChunkWatch(ServerPlayer serverPlayer, LevelChunk levelChunk, ServerLevel serverLevel);
    }

    @FunctionalInterface
    public interface Unwatch {

        /**
         * Fires when a server player stops watching a chunk.
         *
         * @param serverPlayer the player watching the chunk
         * @param chunkPos     the chunk position
         * @param serverLevel  the level the chunk is in
         */
        void onChunkUnwatch(ServerPlayer serverPlayer, ChunkPos chunkPos, ServerLevel serverLevel);
    }
}
