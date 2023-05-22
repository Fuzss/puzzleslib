package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.MinecraftServer;

import java.util.function.BooleanSupplier;

public final class ServerTickEvents {
    public static final EventInvoker<Start> START = EventInvoker.lookup(Start.class);
    public static final EventInvoker<End> END = EventInvoker.lookup(End.class);

    private ServerTickEvents() {

    }

    @FunctionalInterface
    public interface Start {

        /**
         * Fires at the beginning of {@link MinecraftServer#tickServer(BooleanSupplier)}.
         *
         * @param server the current minecraft server instance
         */
        void onStartServerTick(MinecraftServer server);
    }

    @FunctionalInterface
    public interface End {

        /**
         * Fires at the end of {@link MinecraftServer#tickServer(BooleanSupplier)}.
         *
         * @param server the current minecraft server instance
         */
        void onEndServerTick(MinecraftServer server);
    }
}
