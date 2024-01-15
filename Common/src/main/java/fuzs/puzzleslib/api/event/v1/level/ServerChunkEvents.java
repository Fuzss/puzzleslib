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

    }

    @FunctionalInterface
    public interface Load {

        /**
         * Fires before a server chunk is loaded.
         *
         * @param level the server level the chunk is in
         * @param chunk the chunk being loaded
         */
        void onChunkLoad(ServerLevel level, LevelChunk chunk);
    }

    @FunctionalInterface
    public interface Unload {

        /**
         * Fires before a server chunk is unloaded.
         *
         * @param level the server level the chunk is in
         * @param chunk the chunk being unloaded
         */
        void onChunkUnload(ServerLevel level, LevelChunk chunk);
    }

    @FunctionalInterface
    public interface Watch {

        /**
         * Fires when a server player begins watching a chunk, and it has just been sent to a client.
         * <p>Useful for syncing additional chunk data.
         *
         * @param player the player watching the chunk
         * @param chunk  the chunk
         * @param level  the level the chunk is in
         */
        void onChunkWatch(ServerPlayer player, LevelChunk chunk, ServerLevel level);
    }

    @FunctionalInterface
    public interface Unwatch {

        /**
         * Fires when a server player stops watching a chunk.
         *
         * @param player the player watching the chunk
         * @param chunkPos  the chunk position
         * @param level  the level the chunk is in
         */
        void onChunkUnwatch(ServerPlayer player, ChunkPos chunkPos, ServerLevel level);
    }
}
