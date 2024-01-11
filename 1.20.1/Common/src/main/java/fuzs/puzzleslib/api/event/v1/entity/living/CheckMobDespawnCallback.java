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
     * @param mob   the mob the check runs for
     * @param level the current server level
     * @return {@link EventResult#ALLOW} to force the mob to despawn, no matter of circumstances,
     * {@link EventResult#DENY} to prevent the mob from despawning under any circumstances,
     * {@link EventResult#PASS} to allow vanilla behavior to continue executing, with the vanilla despawn check proceeding normally
     */
    EventResult onCheckMobDespawn(Mob mob, ServerLevel level);
}
