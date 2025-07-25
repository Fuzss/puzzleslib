package fuzs.puzzleslib.api.util.v1;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;

import java.util.Objects;

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
     * Called just before an {@link Explosion} is about to be executed for a level.
     *
     * @param serverLevel the level the explosion is happening in
     * @param explosion   the explosion that is about to start
     * @return <code>true</code> to mark the explosion as handled, {@link ServerExplosion#explode()} is not called
     */
    public static boolean onExplosionStart(ServerLevel serverLevel, ServerExplosion explosion) {
        Objects.requireNonNull(serverLevel, "server level is null");
        Objects.requireNonNull(explosion, "explosion is null");
        return ProxyImpl.get().onExplosionStart(serverLevel, explosion);
    }

    /**
     * @return the event loop for running {@link TickTask TickTasks}
     */
    public static BlockableEventLoop<? super TickTask> getBlockableEventLoop(Level level) {
        return ProxyImpl.get().getBlockableEventLoop(level);
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
