package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.MinecraftServer;

public final class ServerLifecycleEvents {
    public static final EventInvoker<Starting> STARTING = EventInvoker.lookup(Starting.class);
    public static final EventInvoker<Started> STARTED = EventInvoker.lookup(Started.class);
    public static final EventInvoker<Stopping> STOPPING = EventInvoker.lookup(Stopping.class);
    public static final EventInvoker<Stopped> STOPPED = EventInvoker.lookup(Stopped.class);

    private ServerLifecycleEvents() {

    }

    @FunctionalInterface
    public interface Starting {

        /**
         * Called before {@link Started}, allows for customization of the server.
         *
         * @param minecraftServer the current server instance
         */
        void onServerStarting(MinecraftServer minecraftServer);
    }

    @FunctionalInterface
    public interface Started {

        /**
         * Called after {@link Starting} when the server is available and ready to play.
         *
         * @param minecraftServer the current server instance
         */
        void onServerStarted(MinecraftServer minecraftServer);
    }

    @FunctionalInterface
    public interface Stopping {

        /**
         * Called before {@link Started} when the server is about to begin a shutdown.
         *
         * @param minecraftServer the current server instance
         */
        void onServerStopping(MinecraftServer minecraftServer);
    }

    @FunctionalInterface
    public interface Stopped {

        /**
         * Called after {@link Stopping} when the server has completely shut down. On the client this happens
         * immediately before returning to the main menu.
         *
         * @param minecraftServer the current server instance
         */
        void onServerStopped(MinecraftServer minecraftServer);
    }
}
