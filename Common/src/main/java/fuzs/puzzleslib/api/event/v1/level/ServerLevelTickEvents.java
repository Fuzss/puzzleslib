package fuzs.puzzleslib.api.event.v1.level;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.function.BooleanSupplier;

public final class ServerLevelTickEvents {
    public static final EventInvoker<Start> START = EventInvoker.lookup(Start.class);
    public static final EventInvoker<End> END = EventInvoker.lookup(End.class);

    private ServerLevelTickEvents() {

    }

    @FunctionalInterface
    public interface Start {

        /**
         * Fires before ticking the server level in {@link MinecraftServer#tickChildren(BooleanSupplier)}.
         *
         * @param server the current minecraft server instance
         * @param level  the server level that is being ticked
         */
        void onStartTick(MinecraftServer server, ServerLevel level);
    }

    @FunctionalInterface
    public interface End {

        /**
         * Fires after ticking the server level in {@link MinecraftServer#tickChildren(BooleanSupplier)}.
         *
         * @param server the current minecraft server instance
         * @param level  the server level that is being ticked
         */
        void onEndTick(MinecraftServer server, ServerLevel level);
    }
}
