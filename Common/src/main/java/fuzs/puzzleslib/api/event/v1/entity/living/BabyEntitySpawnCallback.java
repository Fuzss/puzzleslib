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
     * Called when a child is created from breeding two parents, allows for replacing the child or for preventing any
     * offspring from being spawned.
     *
     * @param partnerMob      the first parent
     * @param otherPartnerMob the other parent
     * @param childMob        the modifiable child
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent a child from being spawned, the love state and breeding cooldown of both parents will be reset as if a child were produced</li>
     *         <li>{@link EventResult#PASS PASS} to allow the offspring to spawn</li>
     *         </ul>
     */
    EventResult onBabyEntitySpawn(Mob partnerMob, Mob otherPartnerMob, MutableValue<AgeableMob> childMob);
}
