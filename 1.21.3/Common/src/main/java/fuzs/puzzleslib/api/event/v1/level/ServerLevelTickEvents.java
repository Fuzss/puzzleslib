package fuzs.puzzleslib.api.event.v1.level;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.function.BooleanSupplier;

public final class ServerLevelTickEvents {
    public static final EventInvoker<Start> START = EventInvoker.lookup(Start.class);
    public static final EventInvoker<End> END = EventInvoker.lookup(End.class);

    private ServerLevelTickEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Start {

        /**
         * Fires before ticking the server level in {@link MinecraftServer#tickChildren(BooleanSupplier)}.
         *
         * @param minecraftServer the current minecraft server instance
         * @param serverLevel     the server level that is being ticked
         */
        void onStartLevelTick(MinecraftServer minecraftServer, ServerLevel serverLevel);
    }

    @FunctionalInterface
    public interface End {

        /**
         * Fires after ticking the server level in {@link MinecraftServer#tickChildren(BooleanSupplier)}.
         *
         * @param minecraftServer the current minecraft server instance
         * @param serverLevel     the server level that is being ticked
         */
        void onEndLevelTick(MinecraftServer minecraftServer, ServerLevel serverLevel);
    }
}
