package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

@FunctionalInterface
public interface CheckMobDespawnCallback {
    EventInvoker<CheckMobDespawnCallback> EVENT = EventInvoker.lookup(CheckMobDespawnCallback.class);

    /**
     * Fires inside of {@link Mob#checkDespawn()} to help determine if the {@link Mob} should despawn.
     *
     * @param mob         the mob the check runs for
     * @param serverLevel the current server level
     * @return <ul>
     *         <li>{@link EventResult#ALLOW ALLOW} to force the mob to despawn, no matter of circumstances</li>
     *         <li>{@link EventResult#DENY DENY} to prevent the mob from despawning under any circumstances</li>
     *         <li>{@link EventResult#PASS PASS} to allow vanilla behavior to continue executing, with the vanilla despawn check proceeding normally</li>
     *         </ul>
     */
    EventResult onCheckMobDespawn(Mob mob, ServerLevel serverLevel);
}
