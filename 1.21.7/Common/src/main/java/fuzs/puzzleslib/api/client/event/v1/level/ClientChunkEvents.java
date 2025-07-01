package fuzs.puzzleslib.api.client.event.v1.level;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.LevelChunk;

public final class ClientChunkEvents {
    public static final EventInvoker<Load> LOAD = EventInvoker.lookup(Load.class);
    public static final EventInvoker<Unload> UNLOAD = EventInvoker.lookup(Unload.class);

    private ClientChunkEvents() {

    }

    @FunctionalInterface
    public interface Load {

        /**
         * Fires before a client chunk is loaded.
         *
         * @param level the client level the chunk is in
         * @param chunk the chunk being loaded
         */
        void onChunkLoad(ClientLevel level, LevelChunk chunk);
    }

    @FunctionalInterface
    public interface Unload {

        /**
         * Fires before a client chunk is unloaded.
         *
         * @param level the client level the chunk is in
         * @param chunk the chunk being unloaded
         */
        void onChunkUnload(ClientLevel level, LevelChunk chunk);
    }
}
