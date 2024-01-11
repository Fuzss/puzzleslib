package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;

@FunctionalInterface
public interface BabyEntitySpawnCallback {
    EventInvoker<BabyEntitySpawnCallback> EVENT = EventInvoker.lookup(BabyEntitySpawnCallback.class);

    /**
     * Called when a child is created from breeding two parents, allows for replacing the child or for preventing any offspring from being spawned.
     *
     * @param animal  the first parent
     * @param partner the other parent
     * @param child   the modifiable child
     * @return {@link EventResult#INTERRUPT} to prevent a child from being spawned, the love state and breeding cooldown of both parents will be reset as if a child were produced,
     * {@link EventResult#PASS} to allow the offspring to spawn
     */
    EventResult onBabyEntitySpawn(Mob animal, Mob partner, MutableValue<AgeableMob> child);
}
