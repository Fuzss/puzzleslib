package fuzs.puzzleslib.api.util.v1;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Provides access to client-only components in the common environment.
 */
public final class ClientCommonHelper {

    private ClientCommonHelper() {
        // NO-OP
    }

    /**
     * @return the client player, throws an exception on dedicated servers
     */
    public static Player getClientPlayer() {
        return ProxyImpl.get().getClientPlayer();
    }

    /**
     * @return the client level, throws an exception on dedicated servers
     */
    public static Level getClientLevel() {
        return ProxyImpl.get().getClientLevel();
    }

    /**
     * @return is the control key pressed, always {@code false} on dedicated servers
     */
    public static boolean hasControlDown() {
        return ProxyImpl.get().hasControlDown();
    }

    /**
     * @return is the shift key pressed, always {@code false} on dedicated servers
     */
    public static boolean hasShiftDown() {
        return ProxyImpl.get().hasShiftDown();
    }

    /**
     * @return is the alt key pressed, always {@code false} on dedicated servers
     */
    public static boolean hasAltDown() {
        return ProxyImpl.get().hasAltDown();
    }
}
