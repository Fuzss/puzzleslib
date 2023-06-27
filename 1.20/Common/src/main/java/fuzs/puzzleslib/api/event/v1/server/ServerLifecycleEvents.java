package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.MinecraftServer;

public final class ServerLifecycleEvents {
    public static final EventInvoker<ServerStarting> STARTING = EventInvoker.lookup(ServerStarting.class);
    public static final EventInvoker<ServerStarted> STARTED = EventInvoker.lookup(ServerStarted.class);
    public static final EventInvoker<ServerStopping> STOPPING = EventInvoker.lookup(ServerStopping.class);
    public static final EventInvoker<ServerStopped> STOPPED = EventInvoker.lookup(ServerStopped.class);

    private ServerLifecycleEvents() {

    }

    @FunctionalInterface
    public interface ServerStarting {

        /**
         * Called before {@link ServerStarted}, allows for customization of the server.
         *
         * @param server the current server instance
         */
        void onServerStarting(MinecraftServer server);
    }

    @FunctionalInterface
    public interface ServerStarted {

        /**
         * Called after {@link ServerStarting} when the server is available and ready to play.
         *
         * @param server the current server instance
         */
        void onServerStarted(MinecraftServer server);
    }

    @FunctionalInterface
    public interface ServerStopping {

        /**
         * Called before {@link ServerStarted} when the server is about to begin a shutdown.
         *
         * @param server the current server instance
         */
        void onServerStopping(MinecraftServer server);
    }

    @FunctionalInterface
    public interface ServerStopped {

        /**
         * Called after {@link ServerStopping} when the server has completely shut down.
         * On the client this happens immediately before returning to the main menu.
         *
         * @param server the current server instance
         */
        void onServerStopped(MinecraftServer server);
    }
}
