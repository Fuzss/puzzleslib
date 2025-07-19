package fuzs.puzzleslib.api.util.v1;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;

/**
 * A helper class containing explosion-related methods.
 */
@Deprecated(forRemoval = true)
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
        return CommonHelper.onExplosionStart(serverLevel, explosion);
    }
}
