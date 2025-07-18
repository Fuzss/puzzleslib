package fuzs.puzzleslib.api.util.v1;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;

import java.util.Objects;

/**
 * A helper class containing explosion-related methods.
 */
public final class ExplosionEventHelper {

    private ExplosionEventHelper() {
        // NO-OP
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
}
