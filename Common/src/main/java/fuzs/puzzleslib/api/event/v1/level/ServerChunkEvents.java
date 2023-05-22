package fuzs.puzzleslib.api.event.v1.level;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class ServerChunkEvents {
    public static final EventInvoker<Load> LOAD = EventInvoker.lookup(Load.class);
    public static final EventInvoker<Unload> UNLOAD = EventInvoker.lookup(Unload.class);

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
        void onLoad(ServerLevel level, ChunkAccess chunk);
    }

    @FunctionalInterface
    public interface Unload {

        /**
         * Fires before a server chunk is unloaded.
         *
         * @param level the server level the chunk is in
         * @param chunk the chunk being unloaded
         */
        void onUnload(ServerLevel level, ChunkAccess chunk);
    }
}
