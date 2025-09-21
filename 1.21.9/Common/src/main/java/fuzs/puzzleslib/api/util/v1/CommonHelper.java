package fuzs.puzzleslib.api.util.v1;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Provides access to common helper methods, some abstracting client-only code.
 */
public final class CommonHelper {

    private CommonHelper() {
        // NO-OP
    }

    /**
     * @return the currently running minecraft server, otherwise {@code null}
     */
    public static MinecraftServer getMinecraftServer() {
        return ProxyImpl.get().getMinecraftServer();
    }

    /**
     * @param level the level
     * @return the event loop for running {@link TickTask TickTasks}
     */
    public static BlockableEventLoop<? super TickTask> getBlockableEventLoop(Level level) {
        return ProxyImpl.get().getBlockableEventLoop(level);
    }

    /**
     * @return the registry access from the current client / server context, otherwise {@code null}
     */
    public static RegistryAccess getRegistryAccess() {
        return ProxyImpl.get().getRegistryAccess();
    }

    /**
     * @return the client player; throws an exception on dedicated servers
     */
    public static Player getClientPlayer() {
        return ProxyImpl.get().getClientPlayer();
    }

    /**
     * @return the client level; throws an exception on dedicated servers
     */
    public static Level getClientLevel() {
        return ProxyImpl.get().getClientLevel();
    }

    /**
     * @return is the control key pressed; always {@code false} on dedicated servers
     */
    public static boolean hasControlDown() {
        return ProxyImpl.get().hasControlDown();
    }

    /**
     * @return is the shift key pressed; always {@code false} on dedicated servers
     */
    public static boolean hasShiftDown() {
        return ProxyImpl.get().hasShiftDown();
    }

    /**
     * @return is the alt key pressed; always {@code false} on dedicated servers
     */
    public static boolean hasAltDown() {
        return ProxyImpl.get().hasAltDown();
    }
}
