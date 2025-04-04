package fuzs.puzzleslib.api.event.v1.level;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public final class ServerLevelEvents {
    public static final EventInvoker<Load> LOAD = EventInvoker.lookup(Load.class);
    public static final EventInvoker<Unload> UNLOAD = EventInvoker.lookup(Unload.class);

    private ServerLevelEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Load {

        /**
         * Fires before a server level is loaded on the minecraft server.
         *
         * @param minecraftServer the current minecraft server instance
         * @param serverLevel     the server level that is being loaded
         */
        void onLevelLoad(MinecraftServer minecraftServer, ServerLevel serverLevel);
    }

    @FunctionalInterface
    public interface Unload {

        /**
         * Fires before a server level is unloaded on the minecraft server.
         *
         * @param minecraftServer the current minecraft server instance
         * @param serverLevel     the server level that is being unloaded
         */
        void onLevelUnload(MinecraftServer minecraftServer, ServerLevel serverLevel);
    }
}
